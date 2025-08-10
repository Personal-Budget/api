package com.biancodavide3.budgeting.api.budget;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/v1/budget")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<Budget> getBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam YearMonth month
    ) {
        return budgetService.getBudget(month, userDetails);
    }

    @PostMapping
    public ResponseEntity<String> addBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid Budget budget
    ) {
        return budgetService.addBudget(budget, userDetails);
    }
}
