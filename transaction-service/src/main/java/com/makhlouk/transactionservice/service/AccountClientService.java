package com.makhlouk.transactionservice.service;

import com.makhlouk.transactionservice.client.AccountRestClient;
import com.makhlouk.transactionservice.domain.dto.AccountDTO;
import com.makhlouk.transactionservice.domain.dto.BalanceUpdateDTO;
import com.makhlouk.transactionservice.domain.exception.AccountServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountClientService {

    private final AccountRestClient accountRestClient;

    @CircuitBreaker(name = "accountService", fallbackMethod = "getAccountFallback")
    public AccountDTO getAccount(UUID accountId) {
        return accountRestClient.getAccountById(accountId);
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "updateBalanceFallback")
    public AccountDTO updateAccountBalance(UUID accountId, double amount) {
        return accountRestClient.updateBalance(accountId, BalanceUpdateDTO.builder().amount(amount).build());
    }

    public AccountDTO getAccountFallback(UUID accountId, Throwable ex) {
        log.error("Circuit breaker triggered: account-service is unavailable for getAccount {}. Cause: {}", accountId, ex.getMessage());
        throw new AccountServiceUnavailableException("Service account-service indisponible (CircuitBreaker ouvert) : " + ex.getMessage());
    }

    public AccountDTO updateBalanceFallback(UUID accountId, double amount, Throwable ex) {
        log.error("Circuit breaker triggered: account-service is unavailable for updateBalance {}. Cause: {}", accountId, ex.getMessage());
        throw new AccountServiceUnavailableException("Service account-service indisponible pour mise à jour du solde : " + ex.getMessage());
    }
}
