package com.akbank.wm.middleware.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.akbank.wm.middleware.core.entity.ServiceErrorLog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceErrorLogRepository extends JpaRepository<ServiceErrorLog, UUID> {

    // Find all error logs by service name
    List<ServiceErrorLog> findByServiceName(String serviceName);

    // Find unresolved error logs
    List<ServiceErrorLog> findByResolvedStatusFalse();

    // Find error logs by severity
    List<ServiceErrorLog> findByErrorSeverity(String severity);

    // Find error logs created after a specific timestamp
    List<ServiceErrorLog> findByRecordDatetimeAfter(LocalDateTime timestamp);

    // Find error logs by service name and unresolved status
    List<ServiceErrorLog> findByServiceNameAndResolvedStatusFalse(String serviceName);

    // Custom query: Find logs by service name and error code
    List<ServiceErrorLog> findByServiceNameAndErrorCode(String serviceName, Integer errorCode);

    // Custom query: Find logs by service name and error code (case-insensitive)
    @Query("SELECT log FROM ServiceErrorLog log WHERE log.recordStatus= :recordStatus AND LOWER(log.serviceName) = LOWER(:serviceName) AND log.resource = :resource AND log.resolvedStatus = :resolvedStatus AND log.referenceDate= :referenceDate")
    List<ServiceErrorLog> findByServiceNameAndResourceAndResolvedStatusAndReferenceDate(String recordStatus,
            String serviceName, String resource, boolean resolvedStatus, LocalDate referenceDate);

    // Count unresolved errors
    long countByResolvedStatusFalse();

    // Mark a specific error log as resolved
    @Query("UPDATE ServiceErrorLog log SET log.resolvedStatus = true WHERE log.recordId = :recordId")
    void markAsResolved(UUID recordId);

    // Optional: Find a specific log by its ID
    Optional<ServiceErrorLog> findById(UUID recordId);
}
