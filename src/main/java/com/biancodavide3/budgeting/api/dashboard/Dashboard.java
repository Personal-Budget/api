package com.biancodavide3.budgeting.api.dashboard;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dashboard {
    private Double monthlyTotalSpent;
    private Double monthlyTotalBudget;
    private String month;
    private String bestMonthOfTheYear;
    private List<DashboardMonth> months;
    private List<DashboardCategory> categories;
}
