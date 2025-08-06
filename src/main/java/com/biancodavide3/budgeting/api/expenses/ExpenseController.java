package com.biancodavide3.budgeting.api.expenses;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(@RequestParam YearMonth month) {
        return expenseService.getExpenses(month);
    }

    @PostMapping
    public ResponseEntity<Expense> addExpense(Expense expense) {
        return expenseService.addExpense(expense);
    }
}
