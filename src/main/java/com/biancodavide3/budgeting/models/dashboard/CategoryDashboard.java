package com.biancodavide3.budgeting.models.dashboard;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CategoryDashboard {
    private String name;
    private double goal;
    private double spent;
}
