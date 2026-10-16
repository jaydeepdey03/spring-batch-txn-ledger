package com.jaydeep.txn_ledger.step.reader;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.jaydeep.txn_ledger.exception.TransientProcessingException;
import com.jaydeep.txn_ledger.model.dto.TransactionCsvRow;
import com.jaydeep.txn_ledger.model.entity.Transaction;
import com.jaydeep.txn_ledger.utils.CsvFieldMapper;

@Component
public class TransactionProcessor implements ItemProcessor<TransactionCsvRow, Transaction> {
    @Override
    public Transaction process(TransactionCsvRow row) {
        simulateTransientFailure();
        String transactionId = CsvFieldMapper.requireNonBlank(row.getTransactionId(), "transactionId");
        String accountNumber = CsvFieldMapper.requireNonBlank(row.getAccountNumber(), "accountNumber");
        String amount = CsvFieldMapper.requireNonBlank(row.getAmount(), "amount");
        String currency = CsvFieldMapper.requireNonBlank(row.getCurrency(), "currency");
        String transactionDate = CsvFieldMapper.requireNonBlank(row.getTransactionDate(), "transactionDate");

        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setAccountNumber(accountNumber);
        transaction.setAmount(CsvFieldMapper.parseAmount(amount));
        transaction.setCurrency(currency);
        transaction.setTransactionDate(CsvFieldMapper.parseDate(transactionDate));
        return transaction;
    }

    private void simulateTransientFailure() {
        // Simulate a transient failure for testing purposes
        if (ThreadLocalRandom.current().nextInt(100) < 5) { // 10% chance of failure
            throw new TransientProcessingException("Simulated transient failure during processing");
        }
    }
}
