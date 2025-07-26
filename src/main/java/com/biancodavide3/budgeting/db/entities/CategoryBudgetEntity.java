package com.biancodavide3.budgeting.db.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "category_budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryBudgetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private BudgetEntity budget;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    private BigDecimal amount;
}
