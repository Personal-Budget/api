package com.biancodavide3.budgeting.api.budget;

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
public class Budget {
    private YearMonth month;
    private BigDecimal totalBudget;
    private List<BudgetCategory> categories;
}
