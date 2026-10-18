package com.jaydeep.txn_ledger.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Business-facing mirror of job run status. Synced via JobExecutionListener
 * (beforeJob/afterJob), independent of Spring's internal BATCH_* schema —
 * queryable by non-batch-dev without knowing Spring Batch internals.
 */
@Entity
@Table(name = "job_run_status")
@Getter
@Setter
@RequiredArgsConstructor 
public class JobRunStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_execution_id", nullable = false, unique = true)
    private Long jobExecutionId;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "status", nullable = false)
    private String status; // STARTED, COMPLETED, FAILED

    @Column(name = "records_read")
    private Long recordsRead;

    @Column(name = "records_written")
    private Long recordsWritten;

    @Column(name = "records_skipped")
    private Long recordsSkipped;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Lob
    @Column(name = "failure_message")
    private String failureMessage;
}
