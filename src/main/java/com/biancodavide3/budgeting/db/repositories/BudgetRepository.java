package com.biancodavide3.budgeting.db.repositories;

import com.biancodavide3.budgeting.db.entities.BudgetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/*
CREATE TABLE budgets (
                         id BIGSERIAL PRIMARY KEY,
                         user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                         month VARCHAR(7) NOT NULL, -- Format: YYYY-MM
                         total_budget NUMERIC(10, 2) NOT NULL,
                         UNIQUE(user_id, month)
);
 */

@Repository
public interface BudgetRepository extends JpaRepository<BudgetEntity, Long>, JpaSpecificationExecutor<BudgetEntity> {
    List<BudgetEntity> findAllByUser_Id(Long userId);
    Page<BudgetEntity> findAllByUser_Id(Long userId, Pageable pageable);
    Optional<BudgetEntity> findByUser_IdAndMonth(Long userId, YearMonth month);
    List<BudgetEntity> findAllByUser_IdAndTotalBudgetGreaterThan(Long userId, BigDecimal totalBudget);
    Page<BudgetEntity> findAllByUser_IdAndTotalBudgetGreaterThan(Long userId, BigDecimal totalBudget, Pageable pageable);
    List<BudgetEntity> findAllByUser_IdAndTotalBudgetLessThan(Long userId, BigDecimal totalBudget);
    Page<BudgetEntity> findAllByUser_IdAndTotalBudgetLessThan(Long userId, BigDecimal totalBudget, Pageable pageable);
}
