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
@Table(name = "CM_WORKING_DAYS", schema = "core")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CmWorkingDays {

    @Id
    @GeneratedValue()
    @Column(name = "RecordID", unique = true, insertable = false, updatable = false)
    private UUID recordID;

    @Column(name = "RecordDatetime", insertable = false, updatable = false)
    private LocalDateTime recordDatetime;

    @Column(name = "RecordStatus", insertable = false)
    private String recordStatus;

    @Column(name = "WorkingDay")
    private LocalDate workingDay;

    @Column(name = "DayOfWeek")
    private String dayOfWeek;

    @Column(name = "FirstDayOfMonth")
    private String firstDayOfMonth;
    @Column(name = "LastDayOfMonth")
    private String lastDayOfMonth;
    @Column(name = "FirstDayOfWeek")
    private String firstDayOfWeek;
    @Column(name = "LastDayOfWeek")
    private String lastDayOfWeek;

}
