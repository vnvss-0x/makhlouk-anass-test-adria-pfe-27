package com.makhlouk.accountservice.web.graphql;

import com.makhlouk.accountservice.domain.AccountStatus;
import com.makhlouk.accountservice.domain.dto.AccountRequestDTO;
import com.makhlouk.accountservice.domain.dto.AccountResponseDTO;
import com.makhlouk.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AccountGraphQLController {

    private final AccountService accountService;

    @QueryMapping
    public List<AccountResponseDTO> accounts() {
        return accountService.getAllAccounts();
    }

    @QueryMapping
    public AccountResponseDTO accountById(@Argument UUID id) {
        return accountService.getAccountById(id);
    }

    @QueryMapping
    public List<AccountResponseDTO> accountsByCustomer(@Argument String customerReference) {
        return accountService.getAccountsByCustomer(customerReference);
    }

    @MutationMapping
    public AccountResponseDTO createAccount(@Argument AccountRequestDTO request) {
        return accountService.createAccount(request);
    }

    @MutationMapping
    public AccountResponseDTO updateBalance(@Argument UUID id, @Argument double amount) {
        return accountService.updateBalance(id, amount);
    }

    @MutationMapping
    public AccountResponseDTO updateStatus(@Argument UUID id, @Argument String status) {
        return accountService.updateStatus(id, AccountStatus.valueOf(status.toUpperCase()));
    }
}
