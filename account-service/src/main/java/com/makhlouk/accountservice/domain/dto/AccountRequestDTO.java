package com.makhlouk.accountservice.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestDTO {

    @NotBlank(message = "Customer reference is required")
    private String customerReference;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code (e.g. EUR, USD, MAD)")
    private String currency;

    @Min(value = 0, message = "Initial balance cannot be negative")
    private double initialBalance;
}
