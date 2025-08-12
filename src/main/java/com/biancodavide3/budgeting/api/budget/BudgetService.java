package com.biancodavide3.budgeting.api.budget;

import com.biancodavide3.budgeting.db.entities.BudgetEntity;
import com.biancodavide3.budgeting.db.entities.BudgetCategoryEntity;
import com.biancodavide3.budgeting.db.entities.CategoryEntity;
import com.biancodavide3.budgeting.db.entities.UserEntity;
import com.biancodavide3.budgeting.db.repositories.BudgetRepository;
import com.biancodavide3.budgeting.db.repositories.CategoryRepository;
import com.biancodavide3.budgeting.db.repositories.UserRepository;
import com.biancodavide3.budgeting.db.repositories.specifications.BudgetSpecification;
import com.biancodavide3.budgeting.db.repositories.specifications.CategorySpecification;
import com.biancodavide3.budgeting.exceptions.budget.BudgetAlreadyExistsException;
import com.biancodavide3.budgeting.exceptions.budget.BudgetNotFoundException;
import com.biancodavide3.budgeting.exceptions.category.CategoryNotFoundException;
import com.biancodavide3.budgeting.exceptions.user.UserNotFoundException;
import com.biancodavide3.budgeting.util.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final UserRepository userRepository;

    public ResponseEntity<BudgetResponse> getBudget(UserDetails userDetails, YearMonth month) {
        Long userId = Users.extractUserId(userDetails);
        BudgetEntity budgetEntity = findBudget(userId, month);
        BudgetResponse budgetResponse = buildBudgetResponse(budgetEntity);
        return ResponseEntity.ok(budgetResponse);
    }

    @Transactional
    public ResponseEntity<Object> addBudget(UserDetails userDetails, BudgetRequest budgetRequest) {
        Long userId = Users.extractUserId(userDetails);

        checkBudgetExists(userId, budgetRequest.getMonth());

        Map<String, CategoryEntity> nameToEntity = mapCategoryNamesToEntities(userId, budgetRequest.getCategories());
        checkAllCategoriesExist(budgetRequest.getCategories(), nameToEntity);

        UserEntity user = findUser(userId);

        BudgetEntity budgetEntity = buildBudgetEntity(user, budgetRequest, nameToEntity);
        budgetEntity = budgetRepository.save(budgetEntity);

        BudgetResponse budgetResponse = buildBudgetResponse(budgetEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetResponse);
    }

    private BudgetEntity findBudget(Long userId, YearMonth month) {
        BudgetSpecification spec = BudgetSpecification.builder()
                .userId(userId)
                .month(month)
                .build();
        return budgetRepository.findOne(spec).orElseThrow(BudgetNotFoundException::new);
    }

    private void checkBudgetExists(Long userId, YearMonth month) {
        BudgetSpecification spec = BudgetSpecification.builder()
                .userId(userId)
                .month(month)
                .build();
        if (budgetRepository.exists(spec)) {
            throw new BudgetAlreadyExistsException();
        }
    }

    private Map<String, CategoryEntity> mapCategoryNamesToEntities(Long userId, List<BudgetCategoryRequest> categories) {
        return categories.stream()
                .map(cat -> categoryRepository.findOne(
                        CategorySpecification.builder()
                                .userId(userId)
                                .nameEquals(cat.getName())
                                .build()
                ).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(CategoryEntity::getName, e -> e));
    }

    private void checkAllCategoriesExist(List<BudgetCategoryRequest> categories, Map<String, CategoryEntity> nameToEntity) {
        boolean allExist = categories.stream().allMatch(cat -> nameToEntity.containsKey(cat.getName()));
        if (!allExist) {
            throw new CategoryNotFoundException("One or more categories not found");
        }
    }

    private UserEntity findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private BudgetEntity buildBudgetEntity(UserEntity user, BudgetRequest budgetRequest, Map<String, CategoryEntity> nameToEntity) {
        List<BudgetCategoryEntity> budgetCategories = budgetRequest.getCategories().stream()
                .map(category -> BudgetCategoryEntity.builder()
                        .category(nameToEntity.get(category.getName()))
                        .budgetedAmount(category.getBudgetedAmount())
                        .build())
                .toList();

        return BudgetEntity.builder()
                .user(user)
                .month(budgetRequest.getMonth())
                .totalBudget(budgetRequest.getTotalBudget())
                .budgetCategories(budgetCategories)
                .build();
    }

    private List<BudgetCategoryResponse> buildCategoriesResponse(BudgetEntity budgetEntity) {
        return budgetEntity.getBudgetCategories().stream()
                .map(budgetCategoryEntity -> BudgetCategoryResponse.builder()
                        .id(budgetCategoryEntity.getId())
                        .categoryId(budgetCategoryEntity.getCategory().getId())
                        .budgetId(budgetCategoryEntity.getBudget().getId())
                        .categoryName(budgetCategoryEntity.getCategory().getName())
                        .budgetedAmount(budgetCategoryEntity.getBudgetedAmount())
                        .build())
                .toList();
    }

    private BudgetResponse buildBudgetResponse(BudgetEntity budgetEntity) {
        List<BudgetCategoryResponse> categoriesResponse = buildCategoriesResponse(budgetEntity);
        return BudgetResponse.builder()
                .id(budgetEntity.getId())
                .userId(budgetEntity.getUser().getId())
                .totalBudget(budgetEntity.getTotalBudget())
                .month(budgetEntity.getMonth())
                .categories(categoriesResponse)
                .build();
    }
}