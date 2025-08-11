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
public class DashboardMonthResponse {
    private String month;
    private BigDecimal amount;
}
