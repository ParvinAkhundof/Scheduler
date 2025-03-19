package com.company.wm.middleware.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "CM_SYSTEM", schema = "core")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CmSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "RecordID")
    private UUID recordID;

    @Column(name = "RecordDatetime")
    private LocalDateTime recordDatetime;

    @Column(name = "RecordStatus")
    private String recordStatus;

    @Column(name = "BusinessDomain")
    private String businessDomain;

    @Column(name = "Parameter")
    private String parameter;

    @Column(name = "ParameterValue")
    private String parameterValue;

    @Column(name = "Description")
    private String description;
}
