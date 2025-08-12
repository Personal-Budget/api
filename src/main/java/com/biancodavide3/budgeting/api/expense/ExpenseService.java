package com.biancodavide3.budgeting.api.expense;

import com.biancodavide3.budgeting.db.entities.CategoryEntity;
import com.biancodavide3.budgeting.db.entities.ExpenseEntity;
import com.biancodavide3.budgeting.db.entities.UserEntity;
import com.biancodavide3.budgeting.db.repositories.CategoryRepository;
import com.biancodavide3.budgeting.db.repositories.ExpenseRepository;
import com.biancodavide3.budgeting.db.repositories.UserRepository;
import com.biancodavide3.budgeting.db.repositories.specifications.CategorySpecification;
import com.biancodavide3.budgeting.db.repositories.specifications.ExpenseSpecification;
import com.biancodavide3.budgeting.exceptions.category.CategoryNotFoundException;
import com.biancodavide3.budgeting.exceptions.user.UserNotFoundException;
import com.biancodavide3.budgeting.util.Users;
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
        Long userId = Users.extractUserId(userDetails);
        List<ExpenseEntity> expenseEntities = findExpensesByUserAndMonth(userId, month);
        List<ExpenseResponse> expenseResponses = buildExpenseResponses(expenseEntities);
        return ResponseEntity.ok(expenseResponses);
    }

    private List<ExpenseEntity> findExpensesByUserAndMonth(Long userId, YearMonth month) {
        LocalDate startDate = LocalDate.of(month.getYear(), month.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        ExpenseSpecification spec = ExpenseSpecification.builder()
                .userId(userId)
                .dateFrom(startDate)
                .dateTo(endDate)
                .build();
        return expenseRepository.findAll(spec);
    }

    private List<ExpenseResponse> buildExpenseResponses(List<ExpenseEntity> entities) {
        return entities.stream()
                .map(this::buildExpenseResponse)
                .toList();
    }

    @Transactional
    public ResponseEntity<Object> addExpense(UserDetails userDetails, ExpenseRequest expenseRequest) {
        Long userId = Users.extractUserId(userDetails);
        CategoryEntity category = findCategory(userId, expenseRequest.getCategory());
        UserEntity user = findUser(userId);
        ExpenseEntity expenseEntity = buildExpenseEntity(expenseRequest, category, user);
        expenseEntity = expenseRepository.save(expenseEntity);
        ExpenseResponse expenseResponse = buildExpenseResponse(expenseEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseResponse);
    }

    private CategoryEntity findCategory(Long userId, String categoryName) {
        CategorySpecification spec = CategorySpecification.builder()
                .userId(userId)
                .nameEquals(categoryName)
                .build();
        return categoryRepository.findOne(spec)
                .orElseThrow(CategoryNotFoundException::new);
    }

    private UserEntity findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private ExpenseEntity buildExpenseEntity(ExpenseRequest request, CategoryEntity category, UserEntity user) {
        return ExpenseEntity.builder()
                .amount(request.getAmount())
                .description(request.getDescription())
                .date(request.getDate())
                .category(category)
                .user(user)
                .build();
    }

    private ExpenseResponse buildExpenseResponse(ExpenseEntity expenseEntity) {
        return ExpenseResponse.builder()
                .id(expenseEntity.getId())
                .userId(expenseEntity.getUser().getId())
                .categoryId(expenseEntity.getCategory().getId())
                .amount(expenseEntity.getAmount())
                .description(expenseEntity.getDescription())
                .date(expenseEntity.getDate())
                .categoryName(expenseEntity.getCategory().getName())
                .build();
    }
}