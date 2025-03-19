package com.company.wm.middleware.core.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.company.wm.middleware.core.entity.CmSystem;

@Repository
public interface CmSystemRepository extends JpaRepository<CmSystem, UUID> {
    List<CmSystem> findByRecordStatus(String recordStatus);
}
