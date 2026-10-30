package com.makhlouk.transactionservice.mcp;

import com.makhlouk.transactionservice.domain.dto.TransactionRequestDTO;
import com.makhlouk.transactionservice.domain.dto.TransferRequestDTO;
import com.makhlouk.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/mcp/transactions")
@RequiredArgsConstructor
public class TransactionMcpTools {

    private final TransactionService transactionService;

    @GetMapping("/tools")
    public List<Map<String, Object>> listTools() {
        return List.of(
                Map.of(
                        "name", "createTransaction",
                        "description", "Record a new transaction for an account with business rules validation",
                        "parameters", Map.of("type", "object", "properties", Map.of(
                                "accountId", Map.of("type", "string"),
                                "amount", Map.of("type", "number"),
                                "currency", Map.of("type", "string"),
                                "merchantCategory", Map.of("type", "string"),
                                "country", Map.of("type", "string")
                        ))
                ),
                Map.of(
                        "name", "transferMoney",
                        "description", "Transfer money between two bank accounts with business validation",
                        "parameters", Map.of("type", "object", "properties", Map.of(
                                "sourceAccountId", Map.of("type", "string"),
                                "destinationAccountId", Map.of("type", "string"),
                                "amount", Map.of("type", "number"),
                                "currency", Map.of("type", "string")
                        ))
                ),
                Map.of(
                        "name", "getAccountTransactions",
                        "description", "Get all transactions for a given account",
                        "parameters", Map.of("type", "object", "properties", Map.of("accountId", Map.of("type", "string")))
                )
        );
    }

    @PostMapping("/execute")
    public Object executeTool(@RequestBody Map<String, Object> toolCall) {
        String toolName = (String) toolCall.get("name");
        Map<String, Object> arguments = (Map<String, Object>) toolCall.get("arguments");

        return switch (toolName) {
            case "createTransaction" -> {
                TransactionRequestDTO request = TransactionRequestDTO.builder()
                        .accountId(UUID.fromString((String) arguments.get("accountId")))
                        .amount(BigDecimal.valueOf(Double.parseDouble(arguments.get("amount").toString())))
                        .currency((String) arguments.get("currency"))
                        .merchantCategory((String) arguments.get("merchantCategory"))
                        .country((String) arguments.get("country"))
                        .build();
                yield transactionService.createTransaction(request);
            }
            case "transferMoney" -> {
                TransferRequestDTO request = TransferRequestDTO.builder()
                        .sourceAccountId(UUID.fromString((String) arguments.get("sourceAccountId")))
                        .destinationAccountId(UUID.fromString((String) arguments.get("destinationAccountId")))
                        .amount(BigDecimal.valueOf(Double.parseDouble(arguments.get("amount").toString())))
                        .currency((String) arguments.get("currency"))
                        .build();
                yield transactionService.transferMoney(request);
            }
            case "getAccountTransactions" -> {
                UUID accountId = UUID.fromString((String) arguments.get("accountId"));
                yield transactionService.getTransactionsByAccountId(accountId);
            }
            default -> throw new IllegalArgumentException("Unknown tool: " + toolName);
        };
    }
}
