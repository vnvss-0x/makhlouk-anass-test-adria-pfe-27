package com.makhlouk.accountservice.service.impl;

import com.makhlouk.accountservice.domain.Account;
import com.makhlouk.accountservice.domain.AccountStatus;
import com.makhlouk.accountservice.domain.dto.AccountRequestDTO;
import com.makhlouk.accountservice.domain.dto.AccountResponseDTO;
import com.makhlouk.accountservice.domain.exception.AccountBlockedException;
import com.makhlouk.accountservice.domain.exception.AccountNotFoundException;
import com.makhlouk.accountservice.domain.exception.InsufficientBalanceException;
import com.makhlouk.accountservice.repository.AccountRepository;
import com.makhlouk.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public AccountResponseDTO createAccount(AccountRequestDTO request) {
        log.info("Creating new account for customer: {}", request.getCustomerReference());

        Account account = Account.builder()
                .customerReference(request.getCustomerReference())
                .status(AccountStatus.ACTIVE)
                .currency(request.getCurrency().toUpperCase())
                .balance(request.getInitialBalance())
                .build();

        Account saved = accountRepository.save(account);
        log.info("Account created successfully with ID: {}", saved.getId());
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountById(UUID id) {
        log.info("Fetching account by ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return mapToDTO(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAllAccounts() {
        log.info("Fetching all bank accounts");
        return accountRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAccountsByCustomer(String customerReference) {
        log.info("Fetching accounts for customer: {}", customerReference);
        return accountRepository.findByCustomerReference(customerReference).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public AccountResponseDTO updateBalance(UUID id, double deltaAmount) {
        log.info("Updating balance for account {} with delta: {}", id, deltaAmount);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        if (account.getStatus() == AccountStatus.BLOCKED) {
            log.warn("Attempted operation on blocked account: {}", id);
            throw new AccountBlockedException(id);
        }

        double newBalance = account.getBalance() + deltaAmount;
        if (newBalance < 0) {
            log.warn("Insufficient balance for account {}. Current: {}, Delta: {}", id, account.getBalance(), deltaAmount);
            throw new InsufficientBalanceException(id, account.getBalance(), Math.abs(deltaAmount));
        }

        account.setBalance(newBalance);
        Account updated = accountRepository.save(account);
        log.info("Account {} new balance: {}", id, updated.getBalance());
        return mapToDTO(updated);
    }

    @Override
    public AccountResponseDTO updateStatus(UUID id, AccountStatus status) {
        log.info("Updating status for account {} to: {}", id, status);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        account.setStatus(status);
        Account updated = accountRepository.save(account);
        log.info("Account {} status updated to: {}", id, updated.getStatus());
        return mapToDTO(updated);
    }

    private AccountResponseDTO mapToDTO(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .customerReference(account.getCustomerReference())
                .status(account.getStatus())
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .build();
    }
}
