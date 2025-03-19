package com.company.wm.middleware.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.wm.middleware.core.entity.CmWorkingDays;
import com.company.wm.middleware.core.entity.JobDefinition;
import com.company.wm.middleware.core.entity.JobExecutionLog;
import com.company.wm.middleware.core.repository.CmWorkingDaysRepository;
import com.company.wm.middleware.core.repository.JobDefinitionRepository;
import com.company.wm.middleware.core.repository.JobExecutionLogRepository;
import com.company.wm.middleware.core.service.CmWorkingDaysService;
import com.company.wm.middleware.core.service.JobSchedulerService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired 
    private JobDefinitionRepository jobDefinitionRepository;

    @Autowired 
    private JobExecutionLogRepository jobExecutionLogRepository;

    @Autowired 
    private CmWorkingDaysService cmWorkingDaysService;

    @Autowired 
    private JobSchedulerService scheduler;

    @Autowired 
    private CmWorkingDaysRepository workingDaysRepository;

    @GetMapping("/definitions/{selectedDate}")
    public List<JobDefinition> getAllJobDefinitions(@PathVariable LocalDate selectedDate) {

        String isWeekEnd = selectedDate.getDayOfWeek() == DayOfWeek.SATURDAY
                || selectedDate.getDayOfWeek() == DayOfWeek.SUNDAY ? "Y" : "N";

        String isHoliday = cmWorkingDaysService.checkDateIfHoliday(selectedDate) ? "Y" : "N";

        String isWorkingDay = isWeekEnd.equals("N") && isHoliday.equals("N") ? "Y" : "N";
        CmWorkingDays lastWorkingDate;
        LocalDate today;
        int date;
        int month;
        String dayOfWeek;

        String firstDayOfMonth;
        String lastDayOfMonth;
        String firstDayOfWeek;
        String lastDayOfWeek;

        if (isWorkingDay.equals("Y")) {
            lastWorkingDate = workingDaysRepository.findSelectedWorkingDay(selectedDate).get(0);
            today = lastWorkingDate.getWorkingDay();
            date = today.getDayOfMonth();
            month = today.getMonthValue();
            dayOfWeek = lastWorkingDate.getDayOfWeek();

            firstDayOfMonth = lastWorkingDate.getFirstDayOfMonth();
            lastDayOfMonth = lastWorkingDate.getLastDayOfMonth();
            firstDayOfWeek = lastWorkingDate.getFirstDayOfWeek();
            lastDayOfWeek = lastWorkingDate.getLastDayOfWeek();

        } else {
            today = selectedDate;
            date = today.getDayOfMonth();
            month = today.getMonthValue();
            dayOfWeek = String.valueOf(today.getDayOfWeek().getValue());

            firstDayOfMonth = "N";
            lastDayOfMonth = "N";
            firstDayOfWeek = "N";
            lastDayOfWeek = "N";
        }

        return jobDefinitionRepository.findAllActiveJobsWithLastStatus(
                isWeekEnd,
                isHoliday,
                isWorkingDay,
                firstDayOfMonth,
                lastDayOfMonth,
                firstDayOfWeek,
                lastDayOfWeek,
                date,
                month,
                dayOfWeek,
                today);
    }

    @GetMapping("/execution-logs/{jobId}/{selectedDate}")
    public List<JobExecutionLog> getExecutionLogsByJobId(@PathVariable Integer jobId,
            @PathVariable LocalDate selectedDate) {
        return jobExecutionLogRepository.findByJobID(jobId, selectedDate);
    }

    @PostMapping("/trigger/{jobId}/{selectedDate}")
    public ResponseEntity<String> triggerJob(@PathVariable Integer jobId, @PathVariable LocalDate selectedDate,
            @RequestBody Map<String, String> credentials) {
        // Your logic to trigger job here
        Optional<JobDefinition> jobDefinition = jobDefinitionRepository.findByJobID(jobId);

        if (jobDefinition.isPresent() && credentials.get("username").equals("wm")
                && credentials.get("password").equals("wm")) {
            CmWorkingDays lastWorkingDate = workingDaysRepository.findSelectedWorkingDay(selectedDate).get(0);
            LocalDate today = lastWorkingDate.getWorkingDay();

            scheduler.retryJobExecution(jobDefinition.get(), today, jobDefinition.get().getMaxRetries(),
                    jobDefinition.get().getDelayInMinutes());
            return ResponseEntity.ok("Job triggered successfully.");
        } else {

            return ResponseEntity.ok("Credentials are false or Job not found with ID: " + jobId);
        }

    }
}
