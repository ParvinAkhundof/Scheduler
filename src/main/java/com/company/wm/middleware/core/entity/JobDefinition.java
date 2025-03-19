package com.company.wm.middleware.core.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "JOB_DEFINITIONS", schema = "core")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecordID")
    private UUID recordID;
    @Column(name = "RecordDatetime")
    private LocalDateTime recordDatetime = LocalDateTime.now();
    @Column(name = "RecordStatus")
    private char recordStatus = 'A';
    @Column(name = "JobID")
    private Integer jobID;
    @Column(name = "JobName")
    private String jobName;
    @Column(name = "JobType")
    private String jobType;
    @Column(name = "Times")
    private String times;
    @Column(name = "PeriodicIntervalMinutes")
    private Integer periodicIntervalMinutes;
    @Column(name = "StartHour")
    private String startHour;
    @Column(name = "EndHour")
    private String endHour;
    @Column(name = "DaysOfWeek")
    private String daysOfWeek;
    @Column(name = "MonthList")
    private String monthList;
    @Column(name = "Sequence")
    private Integer sequence;
    @Column(name = "Method")
    private String method;

    @Column(name = "IsAPIM")
    private String isAPIM;
    @Column(name = "OcpApimSubscriptionKey")
    private String ocpApimSubscriptionKey;
    @Column(name = "ClientID")
    private String clientID;
    @Column(name = "ClientSecret")
    private String clientSecret;
    @Column(name = "RequestBody")
    private String requestBody;
    @Column(name = "DaysOfMonth")
    private String daysOfMonth;

    @Column(name = "Domain")
    private String domain;

    @Column(name = "Description")
    private String description;

    @Column(name = "WorkInHoliday")
    private String workInHoliday;

    @Column(name = "WorkInWeekend")
    private String workInWeekend;

    @Column(name = "FirstWorkingDayOfWeek")
    private String firstWorkingDayOfWeek;

    @Column(name = "LastWorkingDayOfWeek")
    private String lastWorkingDayOfWeek;

    @Column(name = "FirstWorkingDayOfMonth")
    private String firstWorkingDayOfMonth;

    @Column(name = "LastWorkingDayOfMonth")
    private String lastWorkingDayOfMonth;

    @Column(name = "MaxRetries")
    private Integer maxRetries;
    @Column(name = "DelayInMinutes")
    private Integer delayInMinutes;

    @Column(name = "DependentJobIDs")
    private String dependentJobIDs;
    @Column(name = "HttpEndpoint")
    private String httpEndpoint;

    @Column(name = "Status", insertable = false, updatable = false)
    private String status = null;
}
