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
import com.biancodavide3.budgeting.security.CustomUserDetails;
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
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getUser().getId();
        BudgetSpecification budgetSpecification = BudgetSpecification.builder()
                .userId(userId)
                .month(month)
                .build();
        return budgetRepository.findOne(budgetSpecification)
                .map(budgetEntity -> {
                    List<BudgetCategoryResponse> categories = budgetEntity.getBudgetCategories().stream()
                            .map(budgetCategoryEntity -> BudgetCategoryResponse.builder()
                                    .id(budgetCategoryEntity.getId())
                                    .categoryId(budgetCategoryEntity.getCategory().getId())
                                    .budgetId(budgetEntity.getId())
                                    .categoryName(budgetCategoryEntity.getCategory().getName())
                                    .budgetedAmount(budgetCategoryEntity.getBudgetedAmount())
                                    .build())
                            .toList();
                    return BudgetResponse.builder()
                            .id(budgetEntity.getId())
                            .userId(budgetEntity.getUser().getId())
                            .totalBudget(budgetEntity.getTotalBudget())
                            .categories(categories)
                            .month(month)
                            .build();
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<Object> addBudget(UserDetails userDetails, BudgetRequest budgetRequest) {
        Long userId = getUserId(userDetails);

        if (budgetExists(userId, budgetRequest.getMonth())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("BudgetRequest already exists");
        }

        Map<String, CategoryEntity> nameToEntity = mapCategoryNamesToEntities(budgetRequest.getCategories());
        if (!allCategoriesExist(budgetRequest.getCategories(), nameToEntity)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("One or more categories do not exist");
        }

        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        BudgetEntity budgetEntity = buildBudgetEntity(user, budgetRequest, nameToEntity);
        budgetEntity = budgetRepository.save(budgetEntity);

        BudgetResponse budgetResponse = buildBudgetResponse(budgetEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetResponse);
    }

    private Long getUserId(UserDetails userDetails) {
        return ((CustomUserDetails) userDetails).getUser().getId();
    }

    private boolean budgetExists(Long userId, YearMonth month) {
        BudgetSpecification spec = BudgetSpecification.builder().userId(userId).month(month).build();
        return budgetRepository.exists(spec);
    }

    private Map<String, CategoryEntity> mapCategoryNamesToEntities(List<BudgetCategoryRequest> categories) {
        return categories.stream()
                .map(cat -> categoryRepository.findOne(
                        CategorySpecification.builder().nameEquals(cat.getName()).build()
                ).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(CategoryEntity::getName, e -> e));
    }

    private boolean allCategoriesExist(List<BudgetCategoryRequest> categories, Map<String, CategoryEntity> nameToEntity) {
        return categories.stream().allMatch(cat -> nameToEntity.containsKey(cat.getName()));
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

    private BudgetResponse buildBudgetResponse(BudgetEntity budgetEntity) {
        List<BudgetCategoryResponse> categoriesResponse = budgetEntity.getBudgetCategories().stream()
                .map(budgetCategoryEntity -> BudgetCategoryResponse.builder()
                        .id(budgetCategoryEntity.getId())
                        .categoryId(budgetCategoryEntity.getCategory().getId())
                        .budgetId(budgetCategoryEntity.getBudget().getId())
                        .categoryName(budgetCategoryEntity.getCategory().getName())
                        .budgetedAmount(budgetCategoryEntity.getBudgetedAmount())
                        .build())
                .toList();

        return BudgetResponse.builder()
                .id(budgetEntity.getId())
                .userId(budgetEntity.getUser().getId())
                .totalBudget(budgetEntity.getTotalBudget())
                .month(budgetEntity.getMonth())
                .categories(categoriesResponse)
                .build();
    }
}
