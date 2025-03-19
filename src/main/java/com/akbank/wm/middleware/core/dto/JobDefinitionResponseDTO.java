package com.akbank.wm.middleware.core.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobDefinitionResponseDTO {

    private int jobID;
    private String jobName;
    private String jobType;
    private String times;
    private int periodicIntervalMinutes;
    private String startHour;
    private String endHour;
    private String daysOfWeek;
    private String monthList;
    private BigDecimal sequence;
    private String method;
    private Character isAPIM;
    private String ocpApimSubscriptionKey;
    private String requestBody;
    private String daysOfMonth;
    private String domain;
    private String description;
    private Character workInHoliday;
    private Character workInWeekend;
    private Character firstWorkingDayOfWeek;
    private Character lastWorkingDayOfWeek;
    private Character firstWorkingDayOfMonth;
    private int maxRetries;
    private int delayInMinutes;
    private String dependentJobIDs;
    private String httpEndpoint;
    private Character lastWorkingDayOfMonth;
    private String status;

}
