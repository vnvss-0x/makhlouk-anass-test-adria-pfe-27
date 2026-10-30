package com.makhlouk.accountservice.mcp;

import com.makhlouk.accountservice.domain.AccountStatus;
import com.makhlouk.accountservice.domain.dto.AccountRequestDTO;
import com.makhlouk.accountservice.domain.dto.AccountResponseDTO;
import com.makhlouk.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/mcp/accounts")
@RequiredArgsConstructor
public class AccountMcpTools {

    private final AccountService accountService;

    @GetMapping("/tools")
    public List<Map<String, Object>> listTools() {
        return List.of(
                Map.of(
                        "name", "getAccountDetails",
                        "description", "Retrieve account details by UUID",
                        "parameters", Map.of("type", "object", "properties", Map.of("accountId", Map.of("type", "string")))
                ),
                Map.of(
                        "name", "getAllAccounts",
                        "description", "List all bank accounts",
                        "parameters", Map.of("type", "object", "properties", Map.of())
                ),
                Map.of(
                        "name", "updateAccountBalance",
                        "description", "Update account balance (deposit or withdrawal)",
                        "parameters", Map.of("type", "object", "properties", Map.of("accountId", Map.of("type", "string"), "amount", Map.of("type", "number")))
                )
        );
    }

    @PostMapping("/execute")
    public Object executeTool(@RequestBody Map<String, Object> toolCall) {
        String toolName = (String) toolCall.get("name");
        Map<String, Object> arguments = (Map<String, Object>) toolCall.get("arguments");

        return switch (toolName) {
            case "getAccountDetails" -> accountService.getAccountById(UUID.fromString((String) arguments.get("accountId")));
            case "getAllAccounts" -> accountService.getAllAccounts();
            case "updateAccountBalance" -> {
                UUID accountId = UUID.fromString((String) arguments.get("accountId"));
                double amount = Double.parseDouble(arguments.get("amount").toString());
                yield accountService.updateBalance(accountId, amount);
            }
            default -> throw new IllegalArgumentException("Unknown tool: " + toolName);
        };
    }
}
