package com.makhlouk.transactionservice.service;

import com.makhlouk.transactionservice.domain.dto.TransactionRequestDTO;
import com.makhlouk.transactionservice.domain.dto.TransactionResponseDTO;
import com.makhlouk.transactionservice.domain.dto.TransferRequestDTO;
import com.makhlouk.transactionservice.domain.dto.TransferResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionResponseDTO createTransaction(TransactionRequestDTO request);

    TransactionResponseDTO getTransactionById(UUID id);

    List<TransactionResponseDTO> getAllTransactions();

    List<TransactionResponseDTO> getTransactionsByAccountId(UUID accountId);

    TransferResponseDTO transferMoney(TransferRequestDTO request);
}
