package com.biancodavide3.budgeting.api.expenses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {
    private double amount;
    private String description;
    private String category;
    private LocalDate date;
}
