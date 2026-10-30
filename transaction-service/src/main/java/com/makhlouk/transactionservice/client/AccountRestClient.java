package com.makhlouk.transactionservice.client;

import com.makhlouk.transactionservice.domain.dto.AccountDTO;
import com.makhlouk.transactionservice.domain.dto.BalanceUpdateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "account-service", url = "${account.service.url:http://localhost:8081}")
public interface AccountRestClient {

    @GetMapping("/api/accounts/{id}")
    AccountDTO getAccountById(@PathVariable UUID id);

    @PutMapping("/api/accounts/{id}/balance")
    AccountDTO updateBalance(@PathVariable UUID id, @RequestBody BalanceUpdateDTO updateDTO);
}
