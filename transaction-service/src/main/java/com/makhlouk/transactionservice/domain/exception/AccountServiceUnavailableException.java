package com.makhlouk.transactionservice.domain.exception;

public class AccountServiceUnavailableException extends RuntimeException {
    public AccountServiceUnavailableException(String message) {
        super(message);
    }
}
