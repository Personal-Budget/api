package com.biancodavide3.budgeting.db.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private YearMonth month;
    private BigDecimal totalBudget;

    @OneToMany(mappedBy = "budget")
    private List<CategoryBudgetEntity> categoryBudgets;
}