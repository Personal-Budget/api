package com.biancodavide3.budgeting.db.repositories;

import com.biancodavide3.budgeting.db.entities.BudgetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;

/*
CREATE TABLE budgets (
                         id SERIAL PRIMARY KEY,
                         user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                         month VARCHAR(7) NOT NULL, -- Format: YYYY-MM
                         total_budget NUMERIC(10, 2) NOT NULL,
                         UNIQUE(user_id, month)
);
 */

@Repository
public interface BudgetRepository extends JpaRepository<BudgetEntity, Integer> {
    List<BudgetEntity> findAllByUser_Id(Integer userId);
    Page<BudgetEntity> findAllByUser_Id(Integer userId, Pageable pageable);
    List<BudgetEntity> findByUser_IdAndMonth(Integer userId, YearMonth month);
    Page<BudgetEntity> findByUser_IdAndMonth(Integer userId, YearMonth month, Pageable pageable);
    List<BudgetEntity> findAllByUser_IdAndTotalBudgetGreaterThan(Integer userId, BigDecimal totalBudget);
    Page<BudgetEntity> findAllByUser_IdAndTotalBudgetGreaterThan(Integer userId, BigDecimal totalBudget, Pageable pageable);
    List<BudgetEntity> findAllByUser_IdAndTotalBudgetLessThan(Integer userId, BigDecimal totalBudget);
    Page<BudgetEntity> findAllByUser_IdAndTotalBudgetLessThan(Integer userId, BigDecimal totalBudget, Pageable pageable);


}
