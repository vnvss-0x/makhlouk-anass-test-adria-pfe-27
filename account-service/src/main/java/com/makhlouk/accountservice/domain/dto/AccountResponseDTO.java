package com.makhlouk.accountservice.domain.dto;

import com.makhlouk.accountservice.domain.AccountStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDTO {
    private UUID id;
    private String customerReference;
    private AccountStatus status;
    private String currency;
    private double balance;
}
