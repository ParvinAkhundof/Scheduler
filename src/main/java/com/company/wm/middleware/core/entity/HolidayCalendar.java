package com.company.wm.middleware.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "HOLIDAY_CALENDAR", schema = "core")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HolidayCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "RecordID")
    private UUID recordID;

    @Column(name = "RecordDatetime")
    private LocalDateTime recordDatetime;

    @Column(name = "RecordStatus")
    private String recordStatus;

    @Column(name = "HolidayDate")
    private LocalDate holidayDate;

    @Column(name = "Year")
    private String year;

    @Column(name = "HolidayDesc")
    private String holidayDesc;

    @Column(name = "Calendar")
    private String calendar;

    @Column(name = "CalendarDesc")
    private String calendarDesc;

}
