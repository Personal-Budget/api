package com.biancodavide3.budgeting.api.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardCategoryResponse {
    private String name;
    private BigDecimal spent;
    private BigDecimal goal;
}
