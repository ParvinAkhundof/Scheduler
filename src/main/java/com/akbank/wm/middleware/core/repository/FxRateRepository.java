package com.akbank.wm.middleware.core.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.akbank.wm.middleware.core.entity.FxRate;

@Repository
public interface FxRateRepository extends JpaRepository<FxRate, UUID> {

        @Modifying
        @Transactional
        @Query(value = "UPDATE core.MR_FX_RATE SET RecordStatus = 'P' WHERE CONVERT(date, RateDate) = CONVERT(date, ?1)", nativeQuery = true)
        void updateExistRates(LocalDate date);

        @Modifying
        @Transactional
        @Query(value = "Delete from core.MR_FX_RATE WHERE CONVERT(date, RateDate) = CONVERT(date, ?1)", nativeQuery = true)
        void deleteExistRates(LocalDate date);

        // Method to retrieve specific rates based on filters
        List<FxRate> findByRecordStatusAndRateDateAndRateTypeAndCcyCtrIn(String recordStatus, LocalDate rateDate,
                        String rateType, List<String> ccyList);

        @Query(value = "SELECT f FROM FxRate f WHERE f.recordStatus = :recordStatus AND f.ccyCtr = :ccyCtr AND f.rateDate = :rateDate ORDER BY f.rateDate DESC")
        FxRate findLastRateofCurrency(
                        @Param("recordStatus") String recordStatus,
                        @Param("ccyCtr") String ccyCtr,
                        @Param("rateDate") LocalDate rateDate);

}
