package com.makhlouk.accountservice.service;

import com.makhlouk.accountservice.domain.AccountStatus;
import com.makhlouk.accountservice.domain.dto.AccountRequestDTO;
import com.makhlouk.accountservice.domain.dto.AccountResponseDTO;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountResponseDTO createAccount(AccountRequestDTO request);

    AccountResponseDTO getAccountById(UUID id);

    List<AccountResponseDTO> getAllAccounts();

    List<AccountResponseDTO> getAccountsByCustomer(String customerReference);

    AccountResponseDTO updateBalance(UUID id, double deltaAmount);

    AccountResponseDTO updateStatus(UUID id, AccountStatus status);
}
