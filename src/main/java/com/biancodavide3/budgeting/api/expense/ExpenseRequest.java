package com.biancodavide3.budgeting.api.expense;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequest {
    @Digits(integer = 10, fraction = 2, message = "goal must be a valid decimal number with up to 10 digits and 2 decimal places")
    @NotNull(message = "amount cannot be null")
    private BigDecimal amount;
    private String description;
    @NotBlank(message = "category cannot be blank")
    private String category;
    @NotNull(message = "date cannot be null")
    private LocalDate date;
}
