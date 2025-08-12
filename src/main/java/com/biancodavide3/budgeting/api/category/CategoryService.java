package com.biancodavide3.budgeting.api.category;

import com.biancodavide3.budgeting.db.entities.CategoryEntity;
import com.biancodavide3.budgeting.db.entities.UserEntity;
import com.biancodavide3.budgeting.db.repositories.CategoryRepository;
import com.biancodavide3.budgeting.db.repositories.UserRepository;
import com.biancodavide3.budgeting.db.repositories.specifications.CategorySpecification;
import com.biancodavide3.budgeting.exceptions.category.CategoryAlreadyExistsException;
import com.biancodavide3.budgeting.exceptions.user.UserNotFoundException;
import com.biancodavide3.budgeting.util.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ResponseEntity<List<CategoryResponse>> getCategories(UserDetails userDetails) {
        Long userId = Users.extractUserId(userDetails);
        List<CategoryEntity> categoryEntities = findCategoriesByUserId(userId);
        List<CategoryResponse> categoryResponses = buildCategoryResponses(categoryEntities);
        return ResponseEntity.ok(categoryResponses);
    }

    private List<CategoryEntity> findCategoriesByUserId(Long userId) {
        CategorySpecification spec = CategorySpecification.builder()
                .userId(userId)
                .build();
        return categoryRepository.findAll(spec);
    }

    private List<CategoryResponse> buildCategoryResponses(List<CategoryEntity> entities) {
        return entities.stream()
                .map(this::buildCategoryResponse)
                .toList();
    }

    @Transactional
    public ResponseEntity<Object> addCategory(UserDetails userDetails, CategoryRequest categoryRequest) {
        Long userId = Users.extractUserId(userDetails);
        checkCategoryExists(userId, categoryRequest.getName());
        UserEntity user = findUser(userId);
        CategoryEntity categoryEntity = buildCategoryEntity(categoryRequest, user);
        categoryEntity = categoryRepository.save(categoryEntity);
        CategoryResponse categoryResponse = buildCategoryResponse(categoryEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
    }

    private void checkCategoryExists(Long userId, String categoryName) {
        CategorySpecification categorySpecification = CategorySpecification.builder()
                .userId(userId)
                .nameEquals(categoryName)
                .build();
        if (categoryRepository.exists(categorySpecification)) {
            throw new CategoryAlreadyExistsException();
        }
    }

    private UserEntity findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private CategoryEntity buildCategoryEntity(CategoryRequest categoryRequest, UserEntity user) {
        return CategoryEntity.builder()
                .name(categoryRequest.getName())
                .goal(categoryRequest.getGoal())
                .user(user)
                .build();
    }

    private CategoryResponse buildCategoryResponse(CategoryEntity categoryEntity) {
        return CategoryResponse.builder()
                .id(categoryEntity.getId())
                .userId(categoryEntity.getUser().getId())
                .name(categoryEntity.getName())
                .goal(categoryEntity.getGoal())
                .build();
    }
}
