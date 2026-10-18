package com.jaydeep.txn_ledger.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import com.jaydeep.txn_ledger.model.entity.JobRunStatus;
import com.jaydeep.txn_ledger.repository.JobRunStatusRepository;

import java.time.LocalDateTime;

/**
 * Syncs Spring Batch's internal JobExecution lifecycle to business-facing
 * JobRunStatus table. Sets MDC jobExecutionId so all log lines across
 * reader/processor/writer/listeners correlate to this run.
 */
@Component
public class TransactionImportJobListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionImportJobListener.class);

    private final JobRunStatusRepository jobRunStatusRepository;

    public TransactionImportJobListener(JobRunStatusRepository jobRunStatusRepository) {
        this.jobRunStatusRepository = jobRunStatusRepository;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        MDC.put("jobExecutionId", String.valueOf(jobExecution.getId()));
        log.info("Job '{}' starting. JobExecutionId={}", jobExecution.getJobInstance().getJobName(), jobExecution.getId());

        JobRunStatus status = new JobRunStatus();
        status.setJobExecutionId(jobExecution.getId());
        status.setJobName(jobExecution.getJobInstance().getJobName());
        status.setStatus("STARTED");
        status.setStartedAt(LocalDateTime.now());
        jobRunStatusRepository.save(status);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long readCount = 0;
        long writeCount = 0;
        long skipCount = 0;

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            readCount += stepExecution.getReadCount();
            writeCount += stepExecution.getWriteCount();
            skipCount += stepExecution.getSkipCount();
        }

        final long totalReadCount = readCount;
        final long totalWriteCount = writeCount;
        final long totalSkipCount = skipCount;

        jobRunStatusRepository.findByJobExecutionId(jobExecution.getId()).ifPresent(status -> {
            status.setStatus(jobExecution.getStatus().toString());
            status.setFinishedAt(LocalDateTime.now());
            status.setRecordsRead(totalReadCount);
            status.setRecordsWritten(totalWriteCount);
            status.setRecordsSkipped(totalSkipCount);

            if (jobExecution.getStatus().isUnsuccessful()) {
                jobExecution.getAllFailureExceptions().stream()
                        .findFirst()
                        .ifPresent(ex -> status.setFailureMessage(ex.getMessage()));
            }

            jobRunStatusRepository.save(status);
        });

        log.info("Job '{}' finished. Status={}, Read={}, Written={}, Skipped={}",
                jobExecution.getJobInstance().getJobName(), jobExecution.getStatus(),
                readCount, writeCount, skipCount);

        MDC.remove("jobExecutionId");
    }
}