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
public class TransactionRequestDTO {

    @NotNull(message = "accountId is required")
    private UUID accountId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be strictly positive")
    private BigDecimal amount;

    @NotBlank(message = "currency is required")
    private String currency;

    @NotBlank(message = "merchantCategory is required")
    private String merchantCategory;

    @NotBlank(message = "country is required")
    private String country;
}
