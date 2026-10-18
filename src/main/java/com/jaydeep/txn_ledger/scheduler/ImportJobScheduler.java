package com.jaydeep.txn_ledger.scheduler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Cron-triggered launcher. Before every trigger, checks whether the
 * previous JobInstance ended in FAILED/STOPPED and, if so, restarts that
 * exact JobInstance (resuming from the last committed chunk via
 * JobOperator#restart) instead of starting a new one. Only launches a
 * fresh JobInstance (unique JobParameters via timestamp) when there is no
 * previous run or it COMPLETED — avoids JobInstanceAlreadyCompleteException
 * on repeat cron fires.
 */
@Component
public class ImportJobScheduler {

    private static final Logger log = LoggerFactory.getLogger(ImportJobScheduler.class);
    private static final String JOB_NAME = "transactionImportJob";

    private final JobOperator jobOperator;
    private final JobRepository jobRepository;
    private final Job transactionImportJob;

    public ImportJobScheduler(JobOperator jobOperator, JobRepository jobRepository, Job transactionImportJob) {
        this.jobOperator = jobOperator;
        this.jobRepository = jobRepository;
        this.transactionImportJob = transactionImportJob;
    }

    @Scheduled(cron = "${app.scheduler.cron}")
    public void runImportJob() {
        try {
            JobExecution lastExecution = findLastExecution();

            if (lastExecution != null && isRestartable(lastExecution.getStatus())) {
                log.warn("Previous run (JobExecutionId={}) ended with status {}. Restarting same JobInstance instead of starting a new one.",
                        lastExecution.getId(), lastExecution.getStatus());
                jobOperator.restart(lastExecution);
                return;
            }

            JobParameters params = new JobParametersBuilder()
                    .addLong("runTimestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobOperator.run(transactionImportJob, params);
        } catch (Exception e) {
            log.error("Failed to launch transactionImportJob", e);
        }
    }

    private JobExecution findLastExecution() {
        JobInstance lastInstance = jobRepository.getLastJobInstance(JOB_NAME);
        return lastInstance == null ? null : jobRepository.getLastJobExecution(lastInstance);
    }

    private boolean isRestartable(BatchStatus status) {
        return status == BatchStatus.FAILED || status == BatchStatus.STOPPED;
    }
}