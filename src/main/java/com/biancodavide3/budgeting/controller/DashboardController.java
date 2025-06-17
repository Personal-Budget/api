package com.biancodavide3.budgeting.controller;

import com.biancodavide3.budgeting.models.dashboard.CategoryDashboard;
import com.biancodavide3.budgeting.models.dashboard.Dashboard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/dashboard")
public class DashboardController {
    @GetMapping
    public ResponseEntity<Dashboard> getDashboard() {
        CategoryDashboard c1 = CategoryDashboard.builder()
                .name("Food")
                .goal(500.0)
                .spent(300.0)
                .build();
        CategoryDashboard c2 = CategoryDashboard.builder()
                .name("Transport")
                .goal(200.0)
                .spent(150.0)
                .build();
        Dashboard body =  Dashboard.builder()
                .totalSpent(450.0)
                .budget(1000.0)
                .categories(List.of(c1, c2))
                .build();
        return ResponseEntity.ok(body);
    }
}
