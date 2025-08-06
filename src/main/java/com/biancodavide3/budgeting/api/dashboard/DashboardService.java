package com.biancodavide3.budgeting.api.dashboard;

import com.biancodavide3.budgeting.db.entities.CategoryEntity;
import com.biancodavide3.budgeting.db.entities.ExpenseEntity;
import com.biancodavide3.budgeting.db.repositories.BudgetRepository;
import com.biancodavide3.budgeting.db.repositories.CategoryRepository;
import com.biancodavide3.budgeting.db.repositories.ExpenseRepository;
import com.biancodavide3.budgeting.db.repositories.specifications.BudgetSpecification;
import com.biancodavide3.budgeting.db.repositories.specifications.CategorySpecification;
import com.biancodavide3.budgeting.db.repositories.specifications.ExpenseSpecification;
import com.biancodavide3.budgeting.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public ResponseEntity<Dashboard> getDashboard(String month) throws NumberFormatException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        int year = Integer.parseInt(month.substring(0, 4));
        Month monthNumber = Month.of(Integer.parseInt(month.substring(5, 7)));
        LocalDate startDate = LocalDate.of(year, monthNumber, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        double monthlyTotalSpent = getMonthlyTotalSpent(userId, startDate, endDate);
        double monthlyTotalBudget = getMonthlyTotalBudget(userId, year, monthNumber);
        List<DashboardMonth> months = getMonths(userId, year, monthNumber, startDate);
        List<DashboardCategory> categories = getCategories(userId, startDate, endDate);
        DashboardMonth bestMonthOfTheYear = getBestMonthOfTheYear(months);

        Dashboard body = Dashboard.builder()
                .monthlyTotalSpent(monthlyTotalSpent)
                .monthlyTotalBudget(monthlyTotalBudget)
                .month(month)
                .bestMonthOfTheYear(bestMonthOfTheYear.getMonth())
                .months(months)
                .categories(categories)
                .build();
        return ResponseEntity.ok(body);
    }

    private double getMonthlyTotalSpent(Long userId, LocalDate startDate, LocalDate endDate) {
        ExpenseSpecification expenseSpecification = ExpenseSpecification.builder()
                .userId(userId)
                .dateFrom(startDate)
                .dateTo(endDate)
                .build();
        return expenseRepository.findAll(expenseSpecification)
                .stream()
                .mapToDouble(expense -> expense.getAmount().doubleValue())
                .sum();
    }

    private double getMonthlyTotalBudget(Long userId, int year, Month monthNumber) {
        BudgetSpecification budgetSpecification = BudgetSpecification.builder()
                .userId(userId)
                .month(YearMonth.of(year, monthNumber))
                .build();
        return budgetRepository.findOne(budgetSpecification)
                .map(budgetEntity -> budgetEntity.getTotalBudget().doubleValue())
                .orElse(0.0);
    }

    private List<DashboardMonth> getMonths(Long userId, int year, Month monthNumber, LocalDate startDate) {
        List<DashboardMonth> months = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Month monthIteration = Month.of((monthNumber.getValue() + i - 1) % 12 + 1);
            LocalDate from = LocalDate.of(year, monthIteration, 1);
            LocalDate to = startDate.withDayOfMonth(startDate.lengthOfMonth());
            ExpenseSpecification expenseSpecificationIteration = ExpenseSpecification.builder()
                    .userId(userId)
                    .dateFrom(from)
                    .dateTo(to)
                    .build();
            double amount = expenseRepository.findAll(expenseSpecificationIteration)
                    .stream()
                    .mapToDouble(expense -> expense.getAmount().doubleValue())
                    .sum();
            months.add(new DashboardMonth(monthIteration.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), amount));
        }
        return months;
    }

    private List<DashboardCategory> getCategories(Long userId, LocalDate startDate, LocalDate endDate) {
        CategorySpecification categorySpecification = CategorySpecification.builder()
                .userId(userId)
                .build();
        List<CategoryEntity> entities = categoryRepository.findAll(categorySpecification);
        List<DashboardCategory> categories = new ArrayList<>();
        entities.forEach(categoryEntity -> {
            ExpenseSpecification expenseSpec = ExpenseSpecification.builder()
                    .userId(userId)
                    .categoryId(categoryEntity.getId())
                    .dateFrom(startDate)
                    .dateTo(endDate)
                    .build();
            double spent = expenseRepository.findAll(expenseSpec)
                    .stream()
                    .mapToDouble(expense -> expense.getAmount().doubleValue())
                    .sum();
            categories.add(new DashboardCategory(categoryEntity.getName(), categoryEntity.getGoal().doubleValue(), spent));
        });
        return categories;
    }

    private DashboardMonth getBestMonthOfTheYear(List<DashboardMonth> months) {
        return months.stream()
                .min(Comparator.comparingDouble(DashboardMonth::getAmount))
                .orElseThrow();
    }
}