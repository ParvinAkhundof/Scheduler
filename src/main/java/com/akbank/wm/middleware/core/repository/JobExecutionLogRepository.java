package com.akbank.wm.middleware.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.akbank.wm.middleware.core.entity.JobExecutionLog;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@Repository
public interface JobExecutionLogRepository extends JpaRepository<JobExecutionLog, UUID> {
    @Query(value = "Select t.* from (SELECT TOP (1)  *  FROM [core].[JOB_EXECUTION_LOGS] where JobID=?1 and ReferenceDate=?3 order by RecordDatetime desc) t"
            +
            " where t.Status=?2", nativeQuery = true)
    List<JobExecutionLog> existsByJobIDAndStatusAndReferenceDate(Integer jobId, String status, LocalDate referenceDate);

    @Query("SELECT j FROM JobExecutionLog j WHERE j.jobID = :jobId AND j.referenceDate = :date AND j.recordStatus = 'A' ORDER BY j.recordDatetime DESC")
    List<JobExecutionLog> findByJobID(@Param("jobId") int jobId, @Param("date") LocalDate date);

    List<JobExecutionLog> findByJobIDAndStatus(int jobId, String status);
}