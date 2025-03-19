package com.akbank.wm.middleware.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "CM_TRANSFORMATION", schema = "core")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CmTransformation {

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

    @Column(name = "SourceSystem")
    private String sourceSystem;

    @Column(name = "DestinationSystem")
    private String destinationSystem;

    @Column(name = "SourceValue")
    private String sourceValue;

    @Column(name = "DestinationValue")
    private String destinationValue;

    @Column(name = "Description")
    private String description;
}
