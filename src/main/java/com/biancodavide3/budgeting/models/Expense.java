package com.biancodavide3.budgeting.models;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Expense {
    private int id;
    private double amount;
    private int categoryId;
    private String description;
    private LocalDate date;
}
