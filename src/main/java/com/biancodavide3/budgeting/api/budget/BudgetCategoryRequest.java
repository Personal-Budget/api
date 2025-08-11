package com.biancodavide3.budgeting.api.budget;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetCategoryRequest {
    @NotBlank(message = "budget category name cannot be blank")
    private String name;
    @NotNull(message = "budget category amount cannot be null")
    @Digits(integer = 10, fraction = 2, message = "budget category amount must be a valid decimal number with up to 10 digits and 2 decimal places")
    private BigDecimal budgetedAmount;
}
