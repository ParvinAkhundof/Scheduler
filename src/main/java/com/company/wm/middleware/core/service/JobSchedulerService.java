package com.company.wm.middleware.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.wm.middleware.core.dto.HttpCallResponseDTO;
import com.company.wm.middleware.core.entity.JobDefinition;
import com.company.wm.middleware.core.entity.JobExecutionLog;
import com.company.wm.middleware.core.repository.JobExecutionLogRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class JobSchedulerService implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired 
    private HttpService httpService;

    @Autowired 
    private ApimService service;
    @Autowired 
    private JobExecutionLogRepository jobExecutionLogRepository;
    @Autowired 
    private EmailService emailService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // This method is called by Spring to inject the ApplicationContext.
    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    // This method allows us to dynamically get the current instance of
    // JobSchedulerService
    private JobSchedulerService getSelf() {
        return applicationContext.getBean(JobSchedulerService.class);
    }

    // Method for executing job asynchronously
    @Transactional
    public CompletableFuture<ResponseEntity<HttpCallResponseDTO>> executeJobAsync(JobDefinition jobDefinition,
            LocalDate today) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.nanoTime();
            LocalDateTime started = LocalDateTime.now().plusHours(2);
            ResponseEntity<HttpCallResponseDTO> response = executeJob(jobDefinition, today);
            boolean success = "200".equals("" + response.getBody().getStatusCode());

            long endTime = System.nanoTime();
            LocalDateTime ended = LocalDateTime.now().plusHours(2);
            long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds

            if (!success) {
                emailService.errorNotificationEmail(jobDefinition.getJobName(),
                        response.getBody().getResponseBody().toString());
            }
            // Call logJobExecution method using getSelf() to avoid circular reference
            getSelf().logJobExecution(jobDefinition.getJobID(), today, started, ended, success ? "Completed" : "Failed",
                    response.getBody().getResponseBody().toString(), "" + duration);

            return response;
        });
    }

    // Method to check if dependent jobs are completed
    public boolean areDependentJobsCompleted(String dependentJobIDs, LocalDate referenceDate) {
        if (dependentJobIDs == null || dependentJobIDs.isEmpty()) {
            return true; // No dependencies
        }

        Set<Integer> dependentJobIdSet = Stream.of(dependentJobIDs.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        for (Integer jobId : dependentJobIdSet) {
            if (!isJobCompleted(jobId, referenceDate)) {
                return false; // At least one dependent job is not completed
            }
        }
        return true; // All dependent jobs are completed
    }

    // Method to check if a job is completed successfully
    private boolean isJobCompleted(Integer jobId, LocalDate referenceDate) {
        return !jobExecutionLogRepository.existsByJobIDAndStatusAndReferenceDate(jobId, "Completed", referenceDate)
                .isEmpty();
    }

    // Method to execute the job based on jobDefinition and today's date
    private ResponseEntity<HttpCallResponseDTO> executeJob(JobDefinition jobDefinition, LocalDate today) {
        HashMap<String, Object> headerList = new HashMap<>();
        String requestBody = "";

        if (jobDefinition.getRequestBody() != null && !jobDefinition.getRequestBody().isEmpty()) {
            requestBody = jobDefinition.getRequestBody().replace("@DateValue", "" + today);
        }

        if (jobDefinition.getIsAPIM().equals("Y")) {
            String accessToken = service.getAcessToken(jobDefinition.getClientID(), jobDefinition.getClientSecret());
            return ResponseEntity
                    .ok(service.serviceCall(headerList, accessToken, jobDefinition.getOcpApimSubscriptionKey(),
                            jobDefinition.getHttpEndpoint(), jobDefinition.getMethod(), requestBody));
        } else {
            headerList.put("Content-Type", "application/json; utf-8");
            headerList.put("Accept", "application/json");
            return ResponseEntity.ok(httpService.makeHttpCall(jobDefinition.getHttpEndpoint(),
                    jobDefinition.getMethod(), requestBody, headerList));
        }
    }

    // Method to log job execution details
    @Transactional
    public void logJobExecution(int jobId, LocalDate today, LocalDateTime startTime, LocalDateTime endTime,
            String status, String message, String duration) {
        JobExecutionLog executionLog = new JobExecutionLog();
        executionLog.setJobID(jobId);
        executionLog.setStartTime(startTime);
        executionLog.setEndTime(endTime);
        executionLog.setStatus(status);
        executionLog.setMessage(message);
        executionLog.setReferenceDate(today);
        executionLog.setExecutionDuration(duration);
        jobExecutionLogRepository.save(executionLog);
    }

    // Method to retry job execution asynchronously
    @Async
    @Transactional
    public void retryJobExecution(JobDefinition jobDefinition, LocalDate today, int maxRetries, int delayInMinutes) {
        getSelf().executeJobAsync(jobDefinition, today).whenComplete((response, ex) -> {
            boolean success = "200".equals("" + response.getBody().getStatusCode());
            if (!success && maxRetries > 0) {
                scheduler.schedule(
                        () -> getSelf().retryJobExecution(jobDefinition, today, maxRetries - 1, delayInMinutes),
                        delayInMinutes, TimeUnit.MINUTES);
            }
        });
    }
}
