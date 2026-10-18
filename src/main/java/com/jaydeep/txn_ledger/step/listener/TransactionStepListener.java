package com.jaydeep.txn_ledger.step.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

/**
 * Chunk-level logging — read/write/skip/commit counts. Supplements
 * BATCH_STEP_EXECUTION table metadata with structured log lines.
 */
@Component
public class TransactionStepListener implements StepExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionStepListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Step '{}' starting. JobExecutionId={}",
                stepExecution.getStepName(), stepExecution.getJobExecutionId());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Step '{}' finished. Read={}, Written={}, Skipped={}, Commits={}, ExitStatus={}",
                stepExecution.getStepName(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount(),
                stepExecution.getCommitCount(),
                stepExecution.getExitStatus().getExitCode());
        return stepExecution.getExitStatus();
    }
}