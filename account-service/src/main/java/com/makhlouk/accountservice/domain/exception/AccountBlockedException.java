package com.makhlouk.accountservice.domain.exception;

import java.util.UUID;

public class AccountBlockedException extends RuntimeException {
    public AccountBlockedException(UUID id) {
        super("Account is BLOCKED and cannot process operations: " + id);
    }
}
