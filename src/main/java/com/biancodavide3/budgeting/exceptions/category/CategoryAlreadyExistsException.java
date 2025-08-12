package com.biancodavide3.budgeting.exceptions.category;

public class CategoryAlreadyExistsException extends RuntimeException {

    public CategoryAlreadyExistsException() {
        super("Category already exists");
    }

    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}
