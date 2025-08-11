package com.biancodavide3.budgeting.api.expenses;

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
public class ExpenseResponse {
    private Long id;
    private Long userId;
    private Long categoryId;
    private BigDecimal amount;
    private String description;
    private String categoryName;
    private LocalDate date;
}
