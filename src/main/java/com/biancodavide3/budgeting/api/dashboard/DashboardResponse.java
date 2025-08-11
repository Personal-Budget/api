package com.biancodavide3.budgeting.api.dashboard;

import lombok.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private BigDecimal monthlyTotalSpent;
    private BigDecimal monthlyTotalBudget;
    private YearMonth month;
    private DashboardMonthResponse bestMonthOfTheYear;
    private List<DashboardMonthResponse> months;
    private List<DashboardCategoryResponse> categories;
}
