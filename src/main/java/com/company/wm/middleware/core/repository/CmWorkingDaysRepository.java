package com.company.wm.middleware.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.wm.middleware.core.entity.CmWorkingDays;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CmWorkingDaysRepository extends JpaRepository<CmWorkingDays, UUID> {

    @Query(value = "SELECT * FROM core.CM_WORKING_DAYS j " +
            "WHERE j.RecordStatus = 'A' " +
            "AND WorkingDay>='?1' " +
            "order by j.WorkingDay desc", nativeQuery = true)
    List<CmWorkingDays> findActiveWorkingDaysAfterDate(LocalDate date);

    @Query("SELECT w FROM CmWorkingDays w WHERE w.recordStatus = 'A' AND w.workingDay = :date")
    List<CmWorkingDays> findSelectedWorkingDay(@Param("date") LocalDate date);

    @Query(value = "SELECT top(1) * FROM core.CM_WORKING_DAYS  " +
            "WHERE RecordStatus = 'A' " +
            "order by WorkingDay desc", nativeQuery = true)
    List<CmWorkingDays> findLastWorkingDay();
}