package com.makhlouk.transactionservice;

import com.makhlouk.transactionservice.domain.dto.*;
import com.makhlouk.transactionservice.domain.exception.BusinessRuleViolationException;
import com.makhlouk.transactionservice.service.AccountClientService;
import com.makhlouk.transactionservice.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @MockBean
    private AccountClientService accountClientService;

    private UUID accountId;
    private AccountDTO activeAccount;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        activeAccount = AccountDTO.builder()
                .id(accountId)
                .customerReference("CUST-TEST")
                .currency("EUR")
                .balance(500.0)
                .status("ACTIVE")
                .build();
    }

    @Test
    void shouldCreateTransactionWhenRulesRespected() {
        when(accountClientService.getAccount(accountId)).thenReturn(activeAccount);
        when(accountClientService.updateAccountBalance(eq(accountId), anyDouble())).thenReturn(activeAccount);

        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId(accountId)
                .amount(BigDecimal.valueOf(150.0))
                .currency("EUR")
                .merchantCategory("SUPERMARKET")
                .country("MA")
                .build();

        TransactionResponseDTO result = transactionService.createTransaction(request);

        assertNotNull(result.getId());
        assertEquals(accountId, result.getAccountId());
        assertEquals(BigDecimal.valueOf(150.0), result.getAmount());
        verify(accountClientService).updateAccountBalance(accountId, -150.0);
    }

    @Test
    void shouldRejectTransactionWhenAmountExceedsMaxLimit() {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId(accountId)
                .amount(BigDecimal.valueOf(1500.0))
                .currency("EUR")
                .merchantCategory("ELECTRONICS")
                .country("FR")
                .build();

        assertThrows(BusinessRuleViolationException.class, () ->
                transactionService.createTransaction(request)
        );
    }

    @Test
    void shouldRejectTransactionWhenCurrencyMismatch() {
        when(accountClientService.getAccount(accountId)).thenReturn(activeAccount);

        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId(accountId)
                .amount(BigDecimal.valueOf(50.0))
                .currency("USD")
                .merchantCategory("COFFEE")
                .country("US")
                .build();

        assertThrows(BusinessRuleViolationException.class, () ->
                transactionService.createTransaction(request)
        );
    }

    @Test
    void shouldRejectTransactionWhenAccountBlocked() {
        activeAccount.setStatus("BLOCKED");
        when(accountClientService.getAccount(accountId)).thenReturn(activeAccount);

        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .accountId(accountId)
                .amount(BigDecimal.valueOf(50.0))
                .currency("EUR")
                .merchantCategory("COFFEE")
                .country("MA")
                .build();

        assertThrows(BusinessRuleViolationException.class, () ->
                transactionService.createTransaction(request)
        );
    }

    @Test
    void shouldTransferMoneyBetweenTwoAccounts() {
        UUID destId = UUID.randomUUID();
        AccountDTO destAccount = AccountDTO.builder()
                .id(destId)
                .customerReference("CUST-DEST")
                .currency("EUR")
                .balance(100.0)
                .status("ACTIVE")
                .build();

        when(accountClientService.getAccount(accountId)).thenReturn(activeAccount);
        when(accountClientService.getAccount(destId)).thenReturn(destAccount);

        TransferRequestDTO transfer = TransferRequestDTO.builder()
                .sourceAccountId(accountId)
                .destinationAccountId(destId)
                .amount(BigDecimal.valueOf(200.0))
                .currency("EUR")
                .build();

        TransferResponseDTO result = transactionService.transferMoney(transfer);

        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        verify(accountClientService).updateAccountBalance(accountId, -200.0);
        verify(accountClientService).updateAccountBalance(destId, 200.0);
    }
}
