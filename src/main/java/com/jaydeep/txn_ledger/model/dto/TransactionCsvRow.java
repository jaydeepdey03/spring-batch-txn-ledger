package com.jaydeep.txn_ledger.model.dto;

import lombok.Data;

@Data
public class TransactionCsvRow {
    private String transactionId;
    private String accountNumber;
    private String amount;
    private String currency;
    private String transactionDate;
}
