package com.biancodavide3.budgeting.db.repositories;

import com.biancodavide3.budgeting.db.entities.CategoryBudgetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
CREATE TABLE category_budgets (
                                  id SERIAL PRIMARY KEY,
                                  budget_id INTEGER REFERENCES budgets(id) ON DELETE CASCADE,
                                  category_id INTEGER REFERENCES categories(id) ON DELETE CASCADE,
                                  amount NUMERIC(10, 2) NOT NULL,
                                  UNIQUE(budget_id, category_id)
);
 */

@Repository
public interface CategoryBudgetRepository extends JpaRepository<CategoryBudgetEntity, Integer>, JpaSpecificationExecutor<CategoryBudgetEntity> {
    List<CategoryBudgetEntity> findAllByBudget_Id(Integer budgetId);
    Page<CategoryBudgetEntity> findAllByBudget_Id(Integer budgetId, Pageable pageable);
    Optional<CategoryBudgetEntity> findByBudget_IdAndCategory_Id(Integer budgetId, Integer categoryId);
}
