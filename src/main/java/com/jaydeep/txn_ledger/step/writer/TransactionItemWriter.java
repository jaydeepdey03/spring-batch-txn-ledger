package com.jaydeep.txn_ledger.step.writer;

import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

import com.jaydeep.txn_ledger.model.entity.Transaction;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class TransactionItemWriter {
    @Bean
    public ItemWriter<Transaction> transactionItemWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriterBuilder<Transaction> jpaItemWriter = new JpaItemWriterBuilder<>();

        return jpaItemWriter
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true) // Use persist instead of merge for new entities
                .build();
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
