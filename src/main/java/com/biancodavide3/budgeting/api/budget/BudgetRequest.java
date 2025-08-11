package com.biancodavide3.budgeting.api.budget;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetRequest {
    @NotNull(message = "month cannot be null")
    private YearMonth month;
    @NotNull(message = "totalBudget cannot be null")
    @Digits(integer = 10, fraction = 2, message = "totalBudget must be a valid decimal number with up to 10 digits and 2 decimal places")
    private BigDecimal totalBudget;
    @NotEmpty(message = "categories cannot be empty")
    @Valid
    private List<BudgetCategoryRequest> categories;
}
