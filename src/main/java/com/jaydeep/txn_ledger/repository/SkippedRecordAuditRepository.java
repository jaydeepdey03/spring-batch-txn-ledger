package com.jaydeep.txn_ledger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jaydeep.txn_ledger.model.entity.SkippedRecordAudit;

public interface SkippedRecordAuditRepository extends JpaRepository<SkippedRecordAudit, Long> {
    List<SkippedRecordAudit> findByJobExecutionId(Long jobId);
}
