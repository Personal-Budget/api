package com.biancodavide3.budgeting.models.dashboard;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Dashboard {
    private double totalSpent;
    private double budget;
    private List<CategoryDashboard> categories;
}
