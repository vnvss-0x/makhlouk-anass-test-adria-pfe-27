package com.makhlouk.transactionservice;

import com.makhlouk.transactionservice.client.AccountRestClient;
import com.makhlouk.transactionservice.domain.dto.AccountDTO;
import com.makhlouk.transactionservice.domain.exception.AccountServiceUnavailableException;
import com.makhlouk.transactionservice.service.AccountClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AccountClientServiceTest {

    @Autowired
    private AccountClientService accountClientService;

    @MockBean
    private AccountRestClient accountRestClient;

    @Test
    void shouldReturnAccountWhenServiceIsAvailable() {
        UUID accountId = UUID.randomUUID();
        AccountDTO mockAccount = AccountDTO.builder()
                .id(accountId)
                .customerReference("CUST-10")
                .balance(1000.0)
                .currency("EUR")
                .status("ACTIVE")
                .build();

        when(accountRestClient.getAccountById(accountId)).thenReturn(mockAccount);

        AccountDTO result = accountClientService.getAccount(accountId);
        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals("EUR", result.getCurrency());
    }

    @Test
    void shouldTriggerFallbackWhenAccountServiceFails() {
        UUID accountId = UUID.randomUUID();
        when(accountRestClient.getAccountById(accountId)).thenThrow(new RuntimeException("Connection refused"));

        assertThrows(AccountServiceUnavailableException.class, () ->
                accountClientService.getAccount(accountId)
        );
    }
}
