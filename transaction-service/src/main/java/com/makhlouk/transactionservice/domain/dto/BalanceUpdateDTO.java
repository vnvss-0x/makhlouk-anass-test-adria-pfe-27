package com.makhlouk.transactionservice.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceUpdateDTO {
    private double amount;
}
