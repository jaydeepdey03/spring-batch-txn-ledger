package com.jaydeep.txn_ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jaydeep.txn_ledger.model.entity.JobRunStatus;

import java.util.Optional;

public interface JobRunStatusRepository extends JpaRepository<JobRunStatus, Long> {

    Optional<JobRunStatus> findByJobExecutionId(Long jobExecutionId);
}
