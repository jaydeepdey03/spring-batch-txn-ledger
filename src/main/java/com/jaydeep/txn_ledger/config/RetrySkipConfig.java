package com.jaydeep.txn_ledger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;

import com.jaydeep.txn_ledger.exception.TransientProcessingException;

@Configuration
public class RetrySkipConfig {
    @Value("${app.batch.retry-limit:3}")
    private int retryLimit;

    @Bean
    public RetryPolicy transactionRetryPolicy() {
        return RetryPolicy.builder()
                .maxRetries(retryLimit)
                .includes(TransientProcessingException.class)
                .build();
    }
}
