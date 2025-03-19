package com.company.wm.middleware.core.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.company.wm.middleware.core.entity.CmTransformation;

@Repository
public interface CmTransformationRepository extends JpaRepository<CmTransformation, UUID> {
    List<CmTransformation> findByRecordStatusAndSourceSystemAndDestinationSystem(String recordStatus,
            String sourceSystem, String destinationSystem);
}
