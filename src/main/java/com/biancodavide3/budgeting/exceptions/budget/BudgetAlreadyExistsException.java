package com.biancodavide3.budgeting.exceptions.budget;

public class BudgetAlreadyExistsException extends RuntimeException {
    public BudgetAlreadyExistsException() {
        super("Budget already exists");
    }

    public BudgetAlreadyExistsException(String message) {
        super(message);
    }
}
