package com.akbank.wm.middleware.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.akbank.wm.middleware.core.entity.WorkflowDefaults;

import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;

@Repository
public interface WorkflowDefaultsRepository extends JpaRepository<WorkflowDefaults, UUID> {

  // Query derivation method
  List<WorkflowDefaults> findByBusinessDomainAndWorkflowAndOperationAndParameter(String businessDomain, String workflow,
      String operation, String parameter);

  @Modifying
  @Transactional
  @Query("UPDATE WorkflowDefaults w SET w.parameterValue = :parameterValue WHERE w.businessDomain = :businessDomain AND w.workflow = :workflow AND w.operation = :operation AND w.parameter = :parameter")
  int updateParameterValue(
      @Param("parameterValue") String parameterValue,
      @Param("businessDomain") String businessDomain,
      @Param("workflow") String workflow,
      @Param("operation") String operation,
      @Param("parameter") String parameter);
}
