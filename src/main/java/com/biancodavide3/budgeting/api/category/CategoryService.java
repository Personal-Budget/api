package com.biancodavide3.budgeting.api.category;

import com.biancodavide3.budgeting.db.entities.CategoryEntity;
import com.biancodavide3.budgeting.db.entities.UserEntity;
import com.biancodavide3.budgeting.db.repositories.CategoryRepository;
import com.biancodavide3.budgeting.db.repositories.UserRepository;
import com.biancodavide3.budgeting.db.repositories.specifications.CategorySpecification;
import com.biancodavide3.budgeting.security.CustomUserDetails;
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
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        CategorySpecification categorySpecification = CategorySpecification.builder()
                .userId(customUserDetails.getUser().getId())
                .build();
        List<CategoryResponse> categories = categoryRepository.findAll(categorySpecification)
                .stream().map(categoryEntity -> CategoryResponse.builder()
                        .id(categoryEntity.getId())
                        .userId(categoryEntity.getUser().getId())
                        .name(categoryEntity.getName())
                        .goal(categoryEntity.getGoal())
                        .build()).toList();
        return ResponseEntity.ok(categories);
    }

    @Transactional
    public ResponseEntity<Object> addCategory(UserDetails userDetails, CategoryRequest categoryRequest) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getUser().getId();
        CategorySpecification categorySpecification = CategorySpecification.builder()
                .userId(userId)
                .nameEquals(categoryRequest.getName())
                .build();
        if (categoryRepository.exists(categorySpecification)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Category for this user and name already exists");
        }
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .name(categoryRequest.getName())
                .goal(categoryRequest.getGoal())
                .user(user)
                .build();
        categoryEntity = categoryRepository.save(categoryEntity);
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(categoryEntity.getId())
                .userId(categoryEntity.getUser().getId())
                .name(categoryEntity.getName())
                .goal(categoryEntity.getGoal())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
    }
}
