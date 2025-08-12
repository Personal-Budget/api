package com.biancodavide3.budgeting.api.expense;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam YearMonth month
    ) {
        return expenseService.getExpenses(userDetails, month);
    }

    @PostMapping
    public ResponseEntity<Object> addExpense(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid ExpenseRequest expenseRequest
            ) {
        return expenseService.addExpense(userDetails, expenseRequest);
    }
}
