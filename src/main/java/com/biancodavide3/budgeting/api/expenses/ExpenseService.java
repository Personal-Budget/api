package com.biancodavide3.budgeting.api.expenses;

import com.biancodavide3.budgeting.db.entities.CategoryEntity;
import com.biancodavide3.budgeting.db.entities.ExpenseEntity;
import com.biancodavide3.budgeting.db.entities.UserEntity;
import com.biancodavide3.budgeting.db.repositories.CategoryRepository;
import com.biancodavide3.budgeting.db.repositories.ExpenseRepository;
import com.biancodavide3.budgeting.db.repositories.UserRepository;
import com.biancodavide3.budgeting.db.repositories.specifications.CategorySpecification;
import com.biancodavide3.budgeting.db.repositories.specifications.ExpenseSpecification;
import com.biancodavide3.budgeting.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<List<ExpenseResponse>> getExpenses(UserDetails userDetails, YearMonth month) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getUser().getId();
        LocalDate startDate = LocalDate.of(month.getYear(), month.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        ExpenseSpecification expenseSpecification = ExpenseSpecification.builder()
                .userId(userId)
                .dateFrom(startDate)
                .dateTo(endDate)
                .build();
        List<ExpenseResponse> body = expenseRepository.findAll(expenseSpecification).stream()
                .map(expenseEntity -> ExpenseResponse.builder()
                        .id(expenseEntity.getId())
                        .userId(expenseEntity.getUser().getId())
                        .categoryId(expenseEntity.getCategory().getId())
                        .description(expenseEntity.getDescription())
                        .amount(expenseEntity.getAmount())
                        .date(expenseEntity.getDate())
                        .categoryName(expenseEntity.getCategory().getName())
                        .build())
                .toList();
        return ResponseEntity.ok(body);
    }

    @Transactional
    public ResponseEntity<Object> addExpense(UserDetails userDetails, ExpenseRequest expenseRequest) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getUser().getId();
        CategorySpecification categorySpecification = CategorySpecification.builder()
                .userId(userId)
                .nameEquals(expenseRequest.getCategory())
                .build();
        CategoryEntity categoryEntity = categoryRepository.findOne(categorySpecification).orElse(null);
        if (categoryEntity == null) {
            return ResponseEntity.badRequest().body("Category not found for user");
        }
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        ExpenseEntity expenseEntity = ExpenseEntity.builder()
                .amount(expenseRequest.getAmount())
                .description(expenseRequest.getDescription())
                .date(expenseRequest.getDate())
                .category(categoryEntity)
                .user(user)
                .build();
        expenseEntity = expenseRepository.save(expenseEntity);
        ExpenseResponse expenseResponse = ExpenseResponse.builder()
                .id(expenseEntity.getId())
                .userId(expenseEntity.getUser().getId())
                .categoryId(expenseEntity.getCategory().getId())
                .amount(expenseEntity.getAmount())
                .description(expenseEntity.getDescription())
                .date(expenseEntity.getDate())
                .categoryName(expenseEntity.getCategory().getName())
                .build();
        return ResponseEntity.ok(expenseResponse);
    }
}
