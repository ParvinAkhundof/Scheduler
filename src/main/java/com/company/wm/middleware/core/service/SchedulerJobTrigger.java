package com.company.wm.middleware.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.company.wm.middleware.core.entity.CmWorkingDays;
import com.company.wm.middleware.core.entity.JobDefinition;
import com.company.wm.middleware.core.repository.CmWorkingDaysRepository;
import com.company.wm.middleware.core.repository.JobDefinitionRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SchedulerJobTrigger {

@Autowired 
private JobDefinitionRepository jobDefinitionRepository;

@Autowired 
private JobSchedulerService scheduler;

@Autowired 
private CmWorkingDaysService cmWorkingDaysService;

@Autowired 
private CmWorkingDaysRepository workingDaysRepository;

@Scheduled(fixedRate = 60000) // Check every minute
public void scheduleJobs() {

        LocalDateTime now = LocalDateTime.now().plusHours(2);
        int nowInMinutes = now.getHour() * 60 + now.getMinute();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        String hour = now.format(formatter);
        LocalDate actualToday = now.toLocalDate();

        CmWorkingDays lastWorkingDate = workingDaysRepository.findLastWorkingDay().get(0);

        LocalDate today = (actualToday.isBefore(lastWorkingDate.getWorkingDay()) ? actualToday
                : lastWorkingDate.getWorkingDay());
        int date = today.getDayOfMonth();
        int month = today.getMonthValue();
        String dayOfWeek = (actualToday.isBefore(lastWorkingDate.getWorkingDay())
                ? String.valueOf(actualToday.getDayOfWeek().getValue())
                : lastWorkingDate.getDayOfWeek());

        String isWeekEnd = (now.getDayOfWeek() == DayOfWeek.SATURDAY || now.getDayOfWeek() == DayOfWeek.SUNDAY)
                && actualToday.isBefore(lastWorkingDate.getWorkingDay()) ? "Y" : "N";

        String isHoliday = cmWorkingDaysService
                .checkDateIfHoliday(actualToday.isBefore(lastWorkingDate.getWorkingDay()) ? now.toLocalDate()
                        : lastWorkingDate.getWorkingDay()) ? "Y" : "N";

        String isWorkingDay = isWeekEnd.equals("N") && isHoliday.equals("N") ? "Y" : "N";

        String firstDayOfMonth = actualToday.isBefore(lastWorkingDate.getWorkingDay()) ? "N"
                : lastWorkingDate.getFirstDayOfMonth();
        String lastDayOfMonth = actualToday.isBefore(lastWorkingDate.getWorkingDay()) ? "N"
                : lastWorkingDate.getLastDayOfMonth();
        String firstDayOfWeek = actualToday.isBefore(lastWorkingDate.getWorkingDay()) ? "N"
                : lastWorkingDate.getFirstDayOfWeek();
        String lastDayOfWeek = actualToday.isBefore(lastWorkingDate.getWorkingDay()) ? "N"
                : lastWorkingDate.getLastDayOfWeek();
        List<JobDefinition> jobDefinitions = jobDefinitionRepository.findActiveJobDefinitions(nowInMinutes,
                hour,
                today,
                isWeekEnd,
                isHoliday,
                isWorkingDay,
                firstDayOfMonth,
                lastDayOfMonth,
                firstDayOfWeek,
                lastDayOfWeek,
                date,
                month,
                dayOfWeek);


        for (JobDefinition jobDefinition : jobDefinitions) {

            if (scheduler.areDependentJobsCompleted(jobDefinition.getDependentJobIDs(), today)) {

                // Execute the job asynchronously
                scheduler.retryJobExecution(jobDefinition, today, jobDefinition.getMaxRetries(),
                        jobDefinition.getDelayInMinutes());

            } else {
                // Log that dependencies are not met
                scheduler.logJobExecution(jobDefinition.getJobID(), today, LocalDateTime.now().plusHours(2),
                        LocalDateTime.now().plusHours(2), "Waiting", "waiting for dependencies", "0");
       
            }
        }
    }

}
