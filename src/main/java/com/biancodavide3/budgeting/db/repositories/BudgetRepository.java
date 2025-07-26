package com.biancodavide3.budgeting.db.repositories;

import com.biancodavide3.budgeting.db.entities.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<BudgetEntity, Integer> {
}
