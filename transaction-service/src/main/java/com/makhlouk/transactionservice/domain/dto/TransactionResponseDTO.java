package com.makhlouk.transactionservice.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    private UUID id;
    private UUID accountId;
    private BigDecimal amount;
    private String currency;
    private String merchantCategory;
    private String country;
    private Instant createdAt;
}
