package com.biancodavide3.budgeting.api.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        return categoryService.getCategories(userDetails);
    }

    @PostMapping
    public ResponseEntity<Object> addCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CategoryRequest categoryRequest
            ) {
        return categoryService.addCategory(userDetails, categoryRequest);
    }

}
