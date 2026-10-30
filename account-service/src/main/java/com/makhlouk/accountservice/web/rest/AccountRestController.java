package com.makhlouk.accountservice.web.rest;

import com.makhlouk.accountservice.domain.AccountStatus;
import com.makhlouk.accountservice.domain.dto.AccountRequestDTO;
import com.makhlouk.accountservice.domain.dto.AccountResponseDTO;
import com.makhlouk.accountservice.domain.dto.BalanceUpdateDTO;
import com.makhlouk.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts(
            @RequestParam(required = false) String customerReference) {
        if (customerReference != null && !customerReference.isBlank()) {
            return ResponseEntity.ok(accountService.getAccountsByCustomer(customerReference));
        }
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PutMapping("/{id}/balance")
    public ResponseEntity<AccountResponseDTO> updateBalance(
            @PathVariable UUID id,
            @RequestBody BalanceUpdateDTO updateDTO) {
        return ResponseEntity.ok(accountService.updateBalance(id, updateDTO.getAmount()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AccountResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestParam AccountStatus status) {
        return ResponseEntity.ok(accountService.updateStatus(id, status));
    }
}
