package com.biancodavide3.budgeting.models.budget;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Budget {
    private String month;
    private double totalBudget;
    private List<CategoryBudget> categoryBudgets;
}
