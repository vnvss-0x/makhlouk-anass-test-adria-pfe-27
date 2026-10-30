package com.makhlouk.accountservice;

import com.makhlouk.accountservice.domain.AccountStatus;
import com.makhlouk.accountservice.domain.dto.AccountRequestDTO;
import com.makhlouk.accountservice.domain.dto.AccountResponseDTO;
import com.makhlouk.accountservice.domain.exception.AccountBlockedException;
import com.makhlouk.accountservice.domain.exception.InsufficientBalanceException;
import com.makhlouk.accountservice.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Test
    void shouldCreateAndRetrieveAccount() {
        AccountRequestDTO request = AccountRequestDTO.builder()
                .customerReference("TEST-CUST")
                .currency("EUR")
                .initialBalance(1000.0)
                .build();

        AccountResponseDTO created = accountService.createAccount(request);

        assertNotNull(created.getId());
        assertEquals("TEST-CUST", created.getCustomerReference());
        assertEquals("EUR", created.getCurrency());
        assertEquals(AccountStatus.ACTIVE, created.getStatus());
        assertEquals(1000.0, created.getBalance());

        AccountResponseDTO fetched = accountService.getAccountById(created.getId());
        assertEquals(created.getId(), fetched.getId());
        assertEquals(1000.0, fetched.getBalance());
    }

    @Test
    void shouldCreditAndDebitAccountBalance() {
        AccountRequestDTO request = AccountRequestDTO.builder()
                .customerReference("TEST-BALANCE")
                .currency("EUR")
                .initialBalance(500.0)
                .build();

        AccountResponseDTO account = accountService.createAccount(request);

        // Credit 200
        AccountResponseDTO credited = accountService.updateBalance(account.getId(), 200.0);
        assertEquals(700.0, credited.getBalance());

        // Debit 300
        AccountResponseDTO debited = accountService.updateBalance(account.getId(), -300.0);
        assertEquals(400.0, debited.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {
        AccountRequestDTO request = AccountRequestDTO.builder()
                .customerReference("TEST-OVERDRAFT")
                .currency("EUR")
                .initialBalance(100.0)
                .build();

        AccountResponseDTO account = accountService.createAccount(request);

        assertThrows(InsufficientBalanceException.class, () ->
                accountService.updateBalance(account.getId(), -150.0)
        );
    }

    @Test
    void shouldThrowExceptionWhenAccountBlocked() {
        AccountRequestDTO request = AccountRequestDTO.builder()
                .customerReference("TEST-BLOCKED")
                .currency("EUR")
                .initialBalance(500.0)
                .build();

        AccountResponseDTO account = accountService.createAccount(request);
        accountService.updateStatus(account.getId(), AccountStatus.BLOCKED);

        assertThrows(AccountBlockedException.class, () ->
                accountService.updateBalance(account.getId(), 100.0)
        );
    }
}
