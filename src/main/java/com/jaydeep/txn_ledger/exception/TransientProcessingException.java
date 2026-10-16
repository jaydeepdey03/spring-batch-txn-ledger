package com.jaydeep.txn_ledger.exception;

// trigger -- retry
public class TransientProcessingException extends RuntimeException {
    public TransientProcessingException(String message) {
        super(message);
    }
}
