package com.akbank.wm.middleware.core.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "JOB_EXECUTION_LOGS", schema = "core")
@Data
@Getter
@Setter
public class JobExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "RecordID")
    private UUID recordID;

    @Column(name = "RecordDatetime")
    private LocalDateTime recordDatetime = LocalDateTime.now();

    @Column(name = "RecordStatus")
    private char recordStatus = 'A';

    @Column(name = "JobID")
    private int jobID;

    @Column(name = "ReferenceDate")
    private LocalDate referenceDate;

    @Column(name = "StartTime")
    private LocalDateTime startTime;

    @Column(name = "EndTime")
    private LocalDateTime endTime;

    @Column(name = "Status", nullable = false)
    private String status;

    @Column(name = "Message")
    private String message;

    @Column(name = "ExecutionDuration")
    private String executionDuration;

    // Default constructor required by JPA
    public JobExecutionLog() {
    }

    // Constructor for creating a new execution log
    public JobExecutionLog(int jobID, LocalDateTime startTime, String status) {
        this.jobID = jobID;
        this.startTime = startTime;
        this.status = status;
    }

    // Constructors, getters, and setters omitted for brevity
}
