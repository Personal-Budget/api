package com.biancodavide3.budgeting.api.dashboard;

import com.biancodavide3.budgeting.db.entities.BudgetEntity;
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

import java.math.BigDecimal;
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

    public ResponseEntity<DashboardResponse> getDashboard(YearMonth yearMonth) throws NumberFormatException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();
        int year = yearMonth.getYear();
        Month monthNumber = yearMonth.getMonth();
        LocalDate startDate = LocalDate.of(year, monthNumber, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        BigDecimal monthlyTotalSpent = getMonthlyTotalSpent(userId, startDate, endDate);
        BigDecimal monthlyTotalBudget = getMonthlyTotalBudget(userId, year, monthNumber);
        List<DashboardMonthResponse> months = getMonths(userId, year, monthNumber, startDate);
        List<DashboardCategoryResponse> categories = getCategories(userId, startDate, endDate);
        DashboardMonthResponse bestMonthOfTheYear = getBestMonthOfTheYear(months);

        DashboardResponse body = DashboardResponse.builder()
                .monthlyTotalSpent(monthlyTotalSpent)
                .monthlyTotalBudget(monthlyTotalBudget)
                .month(yearMonth)
                .bestMonthOfTheYear(bestMonthOfTheYear)
                .months(months)
                .categories(categories)
                .build();
        return ResponseEntity.ok(body);
    }

    private BigDecimal getMonthlyTotalSpent(Long userId, LocalDate startDate, LocalDate endDate) {
        ExpenseSpecification expenseSpecification = ExpenseSpecification.builder()
                .userId(userId)
                .dateFrom(startDate)
                .dateTo(endDate)
                .build();
        return expenseRepository.findAll(expenseSpecification)
                .stream()
                .map(ExpenseEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getMonthlyTotalBudget(Long userId, int year, Month monthNumber) {
        BudgetSpecification budgetSpecification = BudgetSpecification.builder()
                .userId(userId)
                .month(YearMonth.of(year, monthNumber))
                .build();
        return budgetRepository.findOne(budgetSpecification)
                .map(BudgetEntity::getTotalBudget)
                .orElse(BigDecimal.ZERO);
    }

    private List<DashboardMonthResponse> getMonths(Long userId, int year, Month monthNumber, LocalDate startDate) {
        List<DashboardMonthResponse> months = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Month monthIteration = Month.of((monthNumber.getValue() + i - 1) % 12 + 1);
            LocalDate from = LocalDate.of(year, monthIteration, 1);
            LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
            ExpenseSpecification expenseSpecificationIteration = ExpenseSpecification.builder()
                    .userId(userId)
                    .dateFrom(from)
                    .dateTo(to)
                    .build();
            BigDecimal amount = expenseRepository.findAll(expenseSpecificationIteration)
                    .stream()
                    .map(ExpenseEntity::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            months.add(new DashboardMonthResponse(monthIteration.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), amount));
        }
        return months;
    }

    private List<DashboardCategoryResponse> getCategories(Long userId, LocalDate startDate, LocalDate endDate) {
        CategorySpecification categorySpecification = CategorySpecification.builder()
                .userId(userId)
                .build();
        List<CategoryEntity> entities = categoryRepository.findAll(categorySpecification);
        List<DashboardCategoryResponse> categories = new ArrayList<>();
        entities.forEach(categoryEntity -> {
            ExpenseSpecification expenseSpec = ExpenseSpecification.builder()
                    .userId(userId)
                    .categoryId(categoryEntity.getId())
                    .dateFrom(startDate)
                    .dateTo(endDate)
                    .build();
            BigDecimal spent = expenseRepository.findAll(expenseSpec)
                    .stream()
                    .map(ExpenseEntity::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            categories.add(new DashboardCategoryResponse(categoryEntity.getName(), categoryEntity.getGoal(), spent));
        });
        return categories;
    }

    private DashboardMonthResponse getBestMonthOfTheYear(List<DashboardMonthResponse> months) {
        return months.stream()
                .min(Comparator.comparing(DashboardMonthResponse::getAmount))
                .orElseThrow();
    }
}