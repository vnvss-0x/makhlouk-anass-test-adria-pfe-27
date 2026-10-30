package com.makhlouk.transactionservice.domain.exception;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(UUID id) {
        super("Transaction not found with ID: " + id);
    }
}
