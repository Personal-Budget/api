package com.biancodavide3.budgeting.api.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryGET>> getCategories() {
        return categoryService.getCategories();
    }

    @PostMapping
    public ResponseEntity<CategoryPOST> addCategory(CategoryPOST categoryPOST) {
        return categoryService.addCategory(categoryPOST);
    }

}
