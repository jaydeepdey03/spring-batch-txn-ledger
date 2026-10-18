package com.jaydeep.txn_ledger.config;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;

import com.jaydeep.txn_ledger.exception.InvalidTransactionException;
import com.jaydeep.txn_ledger.exception.TransientProcessingException;
import com.jaydeep.txn_ledger.job.TransactionImportJobListener;
import com.jaydeep.txn_ledger.model.dto.TransactionCsvRow;
import com.jaydeep.txn_ledger.model.entity.Transaction;
import com.jaydeep.txn_ledger.step.listener.TransactionSkipListener;
import com.jaydeep.txn_ledger.step.listener.TransactionStepListener;

/**
 * Composition root for Job/Step. Reader/Processor/Writer/Listener beans
 * injected (DIP) — this class only assembles, never implements step logic.
 */
@Configuration
public class BatchConfig {

    @Value("${app.batch.chunk-size:10}")
    private int chunkSize;

    @Value("${app.batch.skip-limit:5}")
    private int skipLimit;

    @Bean
    public Job transactionImportJob(JobRepository jobRepository,
                                     Step transactionImportStep,
                                     TransactionImportJobListener jobListener) {
        return new JobBuilder("transactionImportJob", jobRepository)
                .listener(jobListener)
                .start(transactionImportStep)
                .build();
    }

    @Bean
    public Step transactionImportStep(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       ItemReader<TransactionCsvRow> transactionCsvItemReader,
                                       ItemProcessor<TransactionCsvRow, Transaction> transactionItemProcessor,
                                       ItemWriter<Transaction> transactionJpaItemWriter,
                                       TransactionSkipListener skipListener,
                                       TransactionStepListener stepListener,
                                       RetryPolicy transactionRetryPolicy) {
        return new StepBuilder("transactionImportStep", jobRepository)
                .<TransactionCsvRow, Transaction>chunk(chunkSize)
                .transactionManager(transactionManager)
                .reader(transactionCsvItemReader)
                .processor(transactionItemProcessor)
                .writer(transactionJpaItemWriter)
                .faultTolerant()
                .skipLimit(skipLimit)
                .skip(InvalidTransactionException.class)
                .retryPolicy(transactionRetryPolicy)
                .retry(TransientProcessingException.class)
                .listener(skipListener)
                .listener(stepListener)
                .build();
    }
}