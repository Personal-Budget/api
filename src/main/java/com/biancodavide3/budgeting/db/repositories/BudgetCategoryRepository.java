package com.biancodavide3.budgeting.db.repositories;

import com.biancodavide3.budgeting.db.entities.BudgetCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetCategoryRepository extends JpaRepository<BudgetCategoryEntity, Long>, JpaSpecificationExecutor<BudgetCategoryEntity> {
}
