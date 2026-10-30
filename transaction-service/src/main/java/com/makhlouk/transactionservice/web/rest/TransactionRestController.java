package com.makhlouk.transactionservice.web.rest;

import com.makhlouk.transactionservice.domain.dto.TransactionRequestDTO;
import com.makhlouk.transactionservice.domain.dto.TransactionResponseDTO;
import com.makhlouk.transactionservice.domain.dto.TransferRequestDTO;
import com.makhlouk.transactionservice.domain.dto.TransferResponseDTO;
import com.makhlouk.transactionservice.service.TransactionService;
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
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Endpoints de gestion et enregistrement des transactions bancaires")
public class TransactionRestController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Enregistrer une nouvelle transaction bancaire")
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(request));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Effectuer un virement d'un compte vers un autre")
    public ResponseEntity<TransferResponseDTO> transferMoney(@Valid @RequestBody TransferRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.transferMoney(request));
    }

    @GetMapping
    @Operation(summary = "Consulter toutes les transactions ou par identifiant de compte")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions(
            @RequestParam(required = false) UUID accountId) {
        if (accountId != null) {
            return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
        }
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une transaction par son identifiant UUID")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
}
