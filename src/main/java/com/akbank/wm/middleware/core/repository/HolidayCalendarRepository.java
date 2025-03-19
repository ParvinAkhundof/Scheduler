package com.akbank.wm.middleware.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.akbank.wm.middleware.core.entity.HolidayCalendar;

import java.util.UUID;
import java.time.LocalDate;

@Repository
public interface HolidayCalendarRepository extends JpaRepository<HolidayCalendar, UUID> {

    boolean existsByCalendarAndRecordStatusAndYearAndHolidayDate(String calendar, String status, String year,
            LocalDate holidayDate);

}