package com.biancodavide3.budgeting.api.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardCategory {
    private String name;
    private Double spent;
    private Double goal;
}
