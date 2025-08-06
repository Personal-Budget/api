package com.biancodavide3.budgeting.api.categories;

import com.biancodavide3.budgeting.db.entities.CategoryEntity;
import com.biancodavide3.budgeting.db.repositories.CategoryRepository;
import com.biancodavide3.budgeting.db.repositories.UserRepository;
import com.biancodavide3.budgeting.db.repositories.specifications.CategorySpecification;
import com.biancodavide3.budgeting.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ResponseEntity<List<CategoryGET>> getCategories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        CategorySpecification categorySpecification = CategorySpecification.builder()
                .userId(userId)
                .build();
        List<CategoryGET> categories = categoryRepository.findAll(categorySpecification)
                .stream().map(categoryEntity -> CategoryGET.builder()
                        .id(categoryEntity.getId())
                        .name(categoryEntity.getName())
                        .goal(categoryEntity.getGoal().doubleValue())
                        .build()).toList();
        return ResponseEntity.ok(categories);
    }

    public ResponseEntity<CategoryPOST> addCategory(CategoryPOST categoryPOST) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .name(categoryPOST.getName())
                .goal(BigDecimal.valueOf(categoryPOST.getGoal()))
                .user(userRepository.findById(userId).orElseThrow())
                .build();
        categoryRepository.save(categoryEntity);
        return ResponseEntity.ok(categoryPOST);
    }
}
