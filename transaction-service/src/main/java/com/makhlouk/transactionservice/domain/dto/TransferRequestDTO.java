package com.makhlouk.transactionservice.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequestDTO {

    @NotNull(message = "sourceAccountId is required")
    private UUID sourceAccountId;

    @NotNull(message = "destinationAccountId is required")
    private UUID destinationAccountId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be strictly positive")
    private BigDecimal amount;

    @NotBlank(message = "currency is required")
    private String currency;
}
