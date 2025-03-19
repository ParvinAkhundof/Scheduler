package com.company.wm.middleware.core.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.wm.middleware.core.entity.CmWorkingDays;
import com.company.wm.middleware.core.repository.CmWorkingDaysRepository;
import com.company.wm.middleware.core.repository.HolidayCalendarRepository;


@Service
public class CmWorkingDaysService {

    @Autowired 
    private CmWorkingDaysRepository workingDaysRepository;

    @Autowired 
    private HolidayCalendarRepository holidayCalendarRepository;

    public LocalDate findNextWorkingDay(LocalDate date) {
        LocalDate nextDay = date.plusDays(1); // Start from the next day
        while (nextDay.getDayOfWeek() == DayOfWeek.SATURDAY || nextDay.getDayOfWeek() == DayOfWeek.SUNDAY
                || checkDateIfHoliday(nextDay)) {
            nextDay = nextDay.plusDays(1); // Increment the day until it's not a weekend
        }
        return nextDay;
    }

    public LocalDate findWorkingDate() {

        List<CmWorkingDays> lastWorkingDateListFromDB = workingDaysRepository.findLastWorkingDay();
        return !lastWorkingDateListFromDB.isEmpty() ? lastWorkingDateListFromDB.get(0).getWorkingDay()
                : LocalDate.now();
    }

    public LocalDate findPreviousWorkingDay(LocalDate date) {
        LocalDate previousDay = date.minusDays(1); // Start from the next day
        while (previousDay.getDayOfWeek() == DayOfWeek.SATURDAY || previousDay.getDayOfWeek() == DayOfWeek.SUNDAY
                || checkDateIfHoliday(previousDay)) {
            previousDay = previousDay.minusDays(1); // Increment the day until it's not a weekend
        }
        return previousDay;
    }

    public boolean checkDateIfHoliday(LocalDate date) {

        return holidayCalendarRepository.existsByCalendarAndRecordStatusAndYearAndHolidayDate("FRB", "A",
                "" + date.getYear(), date);
    }

    @Transactional
    public String saveNextWorkingDate() {
        LocalDate today = findWorkingDate();
        LocalDate nextWorkingDay = findNextWorkingDay(today);
        LocalDate dayAfterNextWorkingDay = findNextWorkingDay(nextWorkingDay);

        String nextDayOfWeek = String.valueOf(nextWorkingDay.getDayOfWeek().getValue());
        int todayOfWeek = Integer.parseInt(String.valueOf(today.getDayOfWeek().getValue()));
        int dayAfterNextDayOfWeek = Integer.parseInt(String.valueOf(dayAfterNextWorkingDay.getDayOfWeek().getValue()));

        String isFirstWorkingDayOfMonth = today.getMonthValue() != nextWorkingDay.getMonthValue() ? "Y" : "N";
        String isLastWorkingDayOfMonth = nextWorkingDay.getMonthValue() != dayAfterNextWorkingDay.getMonthValue() ? "Y"
                : "N";

        String isFirstWorkinDayOfWeek = Integer.parseInt(nextDayOfWeek) < todayOfWeek ? "Y" : "N";
        String isLastWorkingDayOfWeek = Integer.parseInt(nextDayOfWeek) > dayAfterNextDayOfWeek ? "Y" : "N";

        CmWorkingDays workingDays = new CmWorkingDays();
        workingDays.setDayOfWeek(nextDayOfWeek);
        workingDays.setWorkingDay(nextWorkingDay);
        workingDays.setFirstDayOfMonth(isFirstWorkingDayOfMonth);
        workingDays.setLastDayOfMonth(isLastWorkingDayOfMonth);
        workingDays.setFirstDayOfWeek(isFirstWorkinDayOfWeek);
        workingDays.setLastDayOfWeek(isLastWorkingDayOfWeek);
        try {
            workingDaysRepository.save(workingDays);
            return "success";
        } catch (DataIntegrityViolationException dae) {
            return "Database error: " + dae.getMessage();
        } catch (Exception e) {
            return "An unexpected error occurred: " + e.getMessage();
        }
    }

}
