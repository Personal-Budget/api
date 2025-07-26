package com.biancodavide3.budgeting.db.repositories;

import com.biancodavide3.budgeting.db.entities.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Integer> {
}
