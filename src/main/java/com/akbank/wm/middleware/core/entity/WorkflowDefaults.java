package com.akbank.wm.middleware.core.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CM_WORKFLOW_DEFAULTS", schema = "core")
@Getter
@Setter
public class WorkflowDefaults {

    @Id
    @Column(name = "RecordID")
    private UUID recordID;

    @Column(name = "RecordDatetime")
    private LocalDateTime recordDatetime;

    @Column(name = "RecordStatus")
    private String recordStatus;

    @Column(name = "BusinessDomain")
    private String businessDomain;

    @Column(name = "Workflow")
    private String workflow;

    @Column(name = "Operation")
    private String operation;

    @Column(name = "Parameter")
    private String parameter;

    @Column(name = "ParameterValue")
    private String parameterValue;
}
