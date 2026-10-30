package com.makhlouk.accountservice.domain.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID id) {
        super("Account not found with ID: " + id);
    }

    public AccountNotFoundException(String message) {
        super(message);
    }
}
