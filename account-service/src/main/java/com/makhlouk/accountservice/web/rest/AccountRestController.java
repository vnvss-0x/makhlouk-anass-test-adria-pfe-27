package com.makhlouk.accountservice.web.rest;

import com.makhlouk.accountservice.domain.AccountStatus;
import com.makhlouk.accountservice.domain.dto.AccountRequestDTO;
import com.makhlouk.accountservice.domain.dto.AccountResponseDTO;
import com.makhlouk.accountservice.domain.dto.BalanceUpdateDTO;
import com.makhlouk.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Accounts", description = "Endpoints de gestion des comptes bancaires")
public class AccountRestController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Créer un nouveau compte bancaire")
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }

    @GetMapping
    @Operation(summary = "Consulter tous les comptes ou filtrer par référence client")
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts(
            @RequestParam(required = false) String customerReference) {
        if (customerReference != null && !customerReference.isBlank()) {
            return ResponseEntity.ok(accountService.getAccountsByCustomer(customerReference));
        }
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un compte bancaire par son identifiant UUID")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PutMapping("/{id}/balance")
    @Operation(summary = "Mettre à jour le solde d'un compte (crédit ou débit)")
    public ResponseEntity<AccountResponseDTO> updateBalance(
            @PathVariable UUID id,
            @RequestBody BalanceUpdateDTO updateDTO) {
        return ResponseEntity.ok(accountService.updateBalance(id, updateDTO.getAmount()));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Modifier le statut d'un compte (ACTIVE ou BLOCKED)")
    public ResponseEntity<AccountResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestParam AccountStatus status) {
        return ResponseEntity.ok(accountService.updateStatus(id, status));
    }
}
