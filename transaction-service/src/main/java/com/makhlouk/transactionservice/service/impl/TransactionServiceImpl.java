package com.makhlouk.transactionservice.service.impl;

import com.makhlouk.transactionservice.domain.Transaction;
import com.makhlouk.transactionservice.domain.dto.*;
import com.makhlouk.transactionservice.domain.exception.BusinessRuleViolationException;
import com.makhlouk.transactionservice.domain.exception.TransactionNotFoundException;
import com.makhlouk.transactionservice.repository.TransactionRepository;
import com.makhlouk.transactionservice.service.AccountClientService;
import com.makhlouk.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClientService accountClientService;

    @Value("${bank.transaction.max-amount:1000}")
    private double maxAmount;

    @Override
    public TransactionResponseDTO createTransaction(TransactionRequestDTO request) {
        log.info("Processing transaction for account: {}, amount: {}", request.getAccountId(), request.getAmount());

        validateTransactionRules(request.getAccountId(), request.getAmount(), request.getCurrency());

        Transaction transaction = Transaction.builder()
                .accountId(request.getAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency().toUpperCase())
                .merchantCategory(request.getMerchantCategory())
                .country(request.getCountry().toUpperCase())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        // Update account balance (Debit)
        accountClientService.updateAccountBalance(request.getAccountId(), -request.getAmount().doubleValue());
        log.info("Transaction {} completed and account debited successfully", saved.getId());

        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponseDTO getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByAccountId(UUID accountId) {
        return transactionRepository.findByAccountId(accountId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public TransferResponseDTO transferMoney(TransferRequestDTO request) {
        log.info("Processing transfer from {} to {}, amount: {}", request.getSourceAccountId(), request.getDestinationAccountId(), request.getAmount());

        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new BusinessRuleViolationException("Le compte source et destination ne peuvent pas être identiques");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("Le montant du virement doit être strictement positif");
        }

        if (request.getAmount().doubleValue() > maxAmount) {
            throw new BusinessRuleViolationException("Le montant dépasse le plafond autorisé de: " + maxAmount);
        }

        // Validate source account
        AccountDTO source = accountClientService.getAccount(request.getSourceAccountId());
        if (!"ACTIVE".equalsIgnoreCase(source.getStatus())) {
            throw new BusinessRuleViolationException("Le compte source n'est pas actif");
        }
        if (!source.getCurrency().equalsIgnoreCase(request.getCurrency())) {
            throw new BusinessRuleViolationException("La devise du virement ne correspond pas au compte source");
        }
        if (source.getBalance() < request.getAmount().doubleValue()) {
            throw new BusinessRuleViolationException("Solde insuffisant sur le compte source");
        }

        // Validate destination account
        AccountDTO destination = accountClientService.getAccount(request.getDestinationAccountId());
        if (!"ACTIVE".equalsIgnoreCase(destination.getStatus())) {
            throw new BusinessRuleViolationException("Le compte destination n'est pas actif");
        }
        if (!destination.getCurrency().equalsIgnoreCase(request.getCurrency())) {
            throw new BusinessRuleViolationException("La devise du compte destination ne correspond pas");
        }

        // Debit source account
        accountClientService.updateAccountBalance(request.getSourceAccountId(), -request.getAmount().doubleValue());
        Transaction debitTx = transactionRepository.save(Transaction.builder()
                .accountId(request.getSourceAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency().toUpperCase())
                .merchantCategory("TRANSFER_OUT")
                .country("LOCAL")
                .build());

        // Credit destination account
        accountClientService.updateAccountBalance(request.getDestinationAccountId(), request.getAmount().doubleValue());
        Transaction creditTx = transactionRepository.save(Transaction.builder()
                .accountId(request.getDestinationAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency().toUpperCase())
                .merchantCategory("TRANSFER_IN")
                .country("LOCAL")
                .build());

        return TransferResponseDTO.builder()
                .debitTransactionId(debitTx.getId())
                .creditTransactionId(creditTx.getId())
                .sourceAccountId(request.getSourceAccountId())
                .destinationAccountId(request.getDestinationAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency().toUpperCase())
                .status("SUCCESS")
                .timestamp(Instant.now())
                .build();
    }

    private void validateTransactionRules(UUID accountId, BigDecimal amount, String currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("Le montant doit être strictement positif");
        }

        if (amount.doubleValue() > maxAmount) {
            throw new BusinessRuleViolationException(String.format("Le montant %.2f dépasse le plafond autorisé de %.2f", amount.doubleValue(), maxAmount));
        }

        AccountDTO account = accountClientService.getAccount(accountId);
        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new BusinessRuleViolationException("Le compte n'est pas actif (statut: " + account.getStatus() + ")");
        }

        if (!account.getCurrency().equalsIgnoreCase(currency)) {
            throw new BusinessRuleViolationException(String.format("La devise de la transaction (%s) ne correspond pas à celle du compte (%s)", currency, account.getCurrency()));
        }

        if (account.getBalance() < amount.doubleValue()) {
            throw new BusinessRuleViolationException(String.format("Solde insuffisant: solde actuel %.2f, montant demandé %.2f", account.getBalance(), amount.doubleValue()));
        }
    }

    private TransactionResponseDTO mapToDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .merchantCategory(transaction.getMerchantCategory())
                .country(transaction.getCountry())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
