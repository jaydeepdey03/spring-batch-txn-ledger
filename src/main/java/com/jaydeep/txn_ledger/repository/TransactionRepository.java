package com.jaydeep.txn_ledger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jaydeep.txn_ledger.model.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);

    boolean existsByTransactionId(String transactionId);
}
