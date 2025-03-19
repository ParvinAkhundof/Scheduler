package com.akbank.wm.middleware.core.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "MR_FX_RATE", schema = "core")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FxRate {

    @Id
    @GeneratedValue()
    @Column(name = "RecordID", unique = true, insertable = false, updatable = false)
    private UUID recordID;

    @Column(name = "RecordDatetime", insertable = false, updatable = false)
    private LocalDateTime recordDatetime;

    @Column(name = "RecordStatus", insertable = false)
    private String recordStatus;

    @Column(name = "DataSource")
    private String dataSource;

    @Column(name = "RateType")
    private String rateType;

    @Column(name = "RateDate")
    private LocalDate rateDate;

    @Column(name = "Ccy")
    private String ccy;

    @Column(name = "CcyCtr")
    private String ccyCtr;

    @Column(name = "Rate")
    private Double rate;

    @Column(name = "BuyRate")
    private Double buyRate;

    @Column(name = "SellRate")
    private Double sellRate;

    // Getters and setters
}
