package com.biancodavide3.budgeting.api.expenses;

import com.biancodavide3.budgeting.db.entities.CategoryEntity;
import com.biancodavide3.budgeting.db.entities.ExpenseEntity;
import com.biancodavide3.budgeting.db.repositories.CategoryRepository;
import com.biancodavide3.budgeting.db.repositories.ExpenseRepository;
import com.biancodavide3.budgeting.db.repositories.UserRepository;
import com.biancodavide3.budgeting.db.repositories.specifications.CategorySpecification;
import com.biancodavide3.budgeting.db.repositories.specifications.ExpenseSpecification;
import com.biancodavide3.budgeting.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<List<Expense>> getExpenses(YearMonth month) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        LocalDate startDate = LocalDate.of(month.getYear(), month.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        ExpenseSpecification expenseSpecification = ExpenseSpecification.builder()
                .userId(userId)
                .dateFrom(startDate)
                .dateTo(endDate)
                .build();
        List<Expense> body = expenseRepository.findAll(expenseSpecification).stream()
                .map(expenseEntity -> Expense.builder()
                        .description(expenseEntity.getDescription())
                        .amount(expenseEntity.getAmount().doubleValue())
                        .date(expenseEntity.getDate())
                        .category(expenseEntity.getCategory().getName())
                        .build())
                .toList();
        return ResponseEntity.ok(body);
    }

    public ResponseEntity<Expense> addExpense(Expense expense) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        CategorySpecification categorySpecification = CategorySpecification.builder()
                .userId(userId)
                .nameContains(expense.getCategory())
                .build();
        CategoryEntity categoryEntity = categoryRepository.findOne(categorySpecification).orElseThrow();
        ExpenseEntity expenseEntity = ExpenseEntity.builder()
                .amount(BigDecimal.valueOf(expense.getAmount()))
                .description(expense.getDescription())
                .date(expense.getDate())
                .category(categoryEntity)
                .user(userRepository.findById(userId).orElseThrow())
                .build();
        expenseRepository.save(expenseEntity);
        return ResponseEntity.ok(expense);
    }
}
