package com.biancodavide3.budgeting.controller;

import com.biancodavide3.budgeting.models.categories.Category;
import com.biancodavide3.budgeting.models.categories.CategoryPostDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/categories")
public class CategoriesController {

    private int idCounter = 3;
    private List<Category> categories = List.of(
            Category.builder().id(1).name("Food").goal(500.0).build(),
            Category.builder().id(2).name("Transport").goal(200.0).build()
    );

    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public void addCategory(CategoryPostDTO dto) {
        categories.add(
            Category.builder()
                .id(idCounter++)
                .name(dto.getName())
                .goal(dto.getGoal())
                .build()
        );
    }
}
