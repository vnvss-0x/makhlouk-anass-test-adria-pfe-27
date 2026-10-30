package com.makhlouk.accountservice.domain.exception;

import java.util.UUID;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(UUID id, double currentBalance, double requestedAmount) {
        super(String.format("Insufficient balance for account %s: current balance %.2f, requested %.2f", id, currentBalance, requestedAmount));
    }
}
