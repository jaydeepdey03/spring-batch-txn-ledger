package com.jaydeep.txn_ledger.step.listener;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

import com.jaydeep.txn_ledger.model.dto.TransactionCsvRow;
import com.jaydeep.txn_ledger.model.entity.SkippedRecordAudit;
import com.jaydeep.txn_ledger.model.entity.Transaction;
import com.jaydeep.txn_ledger.repository.SkippedRecordAuditRepository;

/**
 * Persists skip/retry-exhaustion details to audit table. SRP: only concern
 * is capturing failure metadata, no business logic.
 */
@Component
public class TransactionSkipListener implements SkipListener<TransactionCsvRow, Transaction> {

    private static final Logger log = LoggerFactory.getLogger(TransactionSkipListener.class);

    private final SkippedRecordAuditRepository auditRepository;

    public TransactionSkipListener(SkippedRecordAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("Skip on read: {}", t.getMessage());
        persistAudit(null, t, "SKIP_READ");
    }

    @Override
    public void onSkipInProcess(TransactionCsvRow item, Throwable t) {
        log.warn("Skip in process for row {}: {}", item, t.getMessage());
        persistAudit(item, t, "SKIP_PROCESS");
    }

    @Override
    public void onSkipInWrite(Transaction item, Throwable t) {
        log.warn("Skip in write for transaction {}: {}", item.getTransactionId(), t.getMessage());
        persistAudit(item, t, "SKIP_WRITE");
    }

    private void persistAudit(Object item, Throwable t, String failureType) {
        Long jobExecutionId = parseJobExecutionId(MDC.get("jobExecutionId"));
        SkippedRecordAudit audit = new SkippedRecordAudit();
        audit.setJobExecutionId(jobExecutionId);
        audit.setStepName("transactionImportStep");
        audit.setRawPayload(String.valueOf(item));
        audit.setFailureReason(t.getMessage());
        audit.setFailureType(failureType);
        audit.setOccurredAt(LocalDateTime.now());
        auditRepository.save(audit);
    }

    private Long parseJobExecutionId(String raw) {
        try {
            return raw == null ? null : Long.valueOf(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
