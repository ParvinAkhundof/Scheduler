package com.akbank.wm.middleware.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "SERVICE_ERROR_LOG", schema = "raw")
@AllArgsConstructor
public class ServiceErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "RecordID", unique = true, insertable = false, updatable = false)
    private UUID recordId; // UUID for RecordID

    @Column(name = "RecordDatetime", insertable = false, updatable = false)
    private LocalDateTime recordDatetime;

    @Column(name = "RecordStatus", insertable = false, updatable = false)
    private String recordStatus;

    @Column(name = "ReferenceDate", nullable = false)
    private LocalDate referenceDate;

    @Column(name = "ReferenceId")
    private String referenceId;

    @Column(name = "Resource", nullable = false)
    private String resource;

    @Column(name = "ServiceName", nullable = false, length = 255)
    private String serviceName;

    @Column(name = "ErrorCode", length = 50)
    private Integer errorCode;

    @Column(name = "ErrorMessage", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String errorMessage;

    @Column(name = "StackTrace", columnDefinition = "NVARCHAR(MAX)")
    private String stackTrace;

    @Column(name = "ErrorSeverity", length = 50)
    private String errorSeverity;

    @Column(name = "AdditionalInfo", columnDefinition = "NVARCHAR(MAX)")
    private String additionalInfo;

    @Column(name = "ResolvedStatus", nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean resolvedStatus;
}
