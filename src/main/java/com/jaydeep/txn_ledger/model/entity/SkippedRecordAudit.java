package com.jaydeep.txn_ledger.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skipped_record_audit")
@Getter
@Setter
@RequiredArgsConstructor
public class SkippedRecordAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_execution_id", nullable = false)
    private Long jobExecutionId;

    @Column(name = "step_name", nullable = false)
    private String stepName;

    @Lob
    @Column(name = "raw_payload")
    private String rawPayload;

    @Lob
    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "failure_type", nullable = false)
    private String failureType;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
}