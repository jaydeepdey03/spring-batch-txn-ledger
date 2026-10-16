package com.jaydeep.txn_ledger.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.jaydeep.txn_ledger.exception.InvalidTransactionException;

/**
 * Shared parsing/validation logic for CSV fields (DRY). Static utility —
 * no state, no Lombok needed.
 */
public final class CsvFieldMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private CsvFieldMapper() {
    }

    public static BigDecimal parseAmount(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidTransactionException("Amount is missing");
        }
        try {
            BigDecimal amount = new BigDecimal(raw.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidTransactionException("Amount must be positive: " + raw);
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new InvalidTransactionException("Invalid amount format: " + raw, e);
        }
    }

    public static LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidTransactionException("Transaction date is missing");
        }
        try {
            return LocalDate.parse(raw.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new InvalidTransactionException("Invalid date format: " + raw, e);
        }
    }

    public static String requireNonBlank(String raw, String fieldName) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidTransactionException(fieldName + " is missing");
        }
        return raw.trim();
    }
}