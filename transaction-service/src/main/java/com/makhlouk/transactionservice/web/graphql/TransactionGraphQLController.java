package com.makhlouk.transactionservice.web.graphql;

import com.makhlouk.transactionservice.domain.dto.TransactionRequestDTO;
import com.makhlouk.transactionservice.domain.dto.TransactionResponseDTO;
import com.makhlouk.transactionservice.domain.dto.TransferRequestDTO;
import com.makhlouk.transactionservice.domain.dto.TransferResponseDTO;
import com.makhlouk.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TransactionGraphQLController {

    private final TransactionService transactionService;

    @QueryMapping
    public List<TransactionResponseDTO> transactions() {
        return transactionService.getAllTransactions();
    }

    @QueryMapping
    public TransactionResponseDTO transactionById(@Argument UUID id) {
        return transactionService.getTransactionById(id);
    }

    @QueryMapping
    public List<TransactionResponseDTO> transactionsByAccountId(@Argument UUID accountId) {
        return transactionService.getTransactionsByAccountId(accountId);
    }

    @MutationMapping
    public TransactionResponseDTO createTransaction(@Argument TransactionRequestDTO request) {
        return transactionService.createTransaction(request);
    }

    @MutationMapping
    public TransferResponseDTO transferMoney(@Argument TransferRequestDTO request) {
        return transactionService.transferMoney(request);
    }
}
