package com.biancodavide3.budgeting.exceptions.budget;

public class BudgetNotFoundException extends RuntimeException {

    public BudgetNotFoundException() {
        super("Budget not found");
    }

    public BudgetNotFoundException(String message) {
        super(message);
    }
}
