package com.biancodavide3.budgeting.api.budget;

import com.biancodavide3.budgeting.db.entities.BudgetEntity;
import com.biancodavide3.budgeting.db.entities.CategoryBudgetEntity;
import com.biancodavide3.budgeting.db.entities.CategoryEntity;
import com.biancodavide3.budgeting.db.repositories.BudgetRepository;
import com.biancodavide3.budgeting.db.repositories.CategoryRepository;
import com.biancodavide3.budgeting.db.repositories.specifications.BudgetSpecification;
import com.biancodavide3.budgeting.db.repositories.specifications.CategorySpecification;
import com.biancodavide3.budgeting.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<Budget> getBudget(YearMonth month, UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        var budgetSpecification = BudgetSpecification.builder()
                .userId(customUserDetails.getUser().getId())
                .month(month)
                .build();
        return budgetRepository.findOne(budgetSpecification)
                .map(budgetEntity -> {
                    List<BudgetCategory> categories = budgetEntity.getCategoryBudgets().stream()
                            .map(categoryBudgetEntity -> BudgetCategory.builder()
                                    .name(categoryBudgetEntity.getCategory().getName())
                                    .amount(categoryBudgetEntity.getAmount())
                                    .build())
                            .collect(Collectors.toList());
                    return Budget.builder()
                            .totalBudget(budgetEntity.getTotalBudget())
                            .categories(categories)
                            .month(month)
                            .build();
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<String> addBudget(Budget budget, UserDetails userDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        BudgetSpecification budgetSpecification = BudgetSpecification.builder()
                .userId(customUserDetails.getUser().getId())
                .month(budget.getMonth())
                .build();
        if (budgetRepository.exists(budgetSpecification)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Budget already exists");
        }

        List<BudgetCategory> categories = budget.getCategories();

        Map<String, CategoryEntity> nameToEntity = categories.stream()
                .map(cat -> categoryRepository.findOne(
                                CategorySpecification.builder()
                                        .nameEquals(cat.getName())
                                        .build()
                        )
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(CategoryEntity::getName, e -> e));

        boolean missingCategory = categories.stream()
                .anyMatch(cat -> !nameToEntity.containsKey(cat.getName()));
        if (missingCategory) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("One or more categories do not exist");
        }

        List<CategoryBudgetEntity> categoryBudgetEntities = categories.stream()
                .map(cat -> CategoryBudgetEntity.builder()
                        .category(nameToEntity.get(cat.getName()))
                        .amount(cat.getAmount())
                        .build())
                .toList();

        BudgetEntity budgetEntity = BudgetEntity.builder()
                .user(customUserDetails.getUser())
                .month(budget.getMonth())
                .totalBudget(budget.getTotalBudget())
                .categoryBudgets(categoryBudgetEntities)
                .build();

        budgetRepository.save(budgetEntity);

        return ResponseEntity.ok("Budget created successfully");
    }
}
