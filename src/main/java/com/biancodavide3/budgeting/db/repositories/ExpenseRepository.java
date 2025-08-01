package com.biancodavide3.budgeting.db.repositories;

import com.biancodavide3.budgeting.db.entities.ExpenseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/*
CREATE TABLE expenses (
                          id SERIAL PRIMARY KEY,
                          user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                          category_id INTEGER REFERENCES categories(id) ON DELETE SET NULL,
                          amount NUMERIC(10, 2) NOT NULL,
                          description TEXT,
                          date DATE NOT NULL
);
 */

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Integer> {
    List<ExpenseEntity> findAllByUser_Id(Integer userId);
    Page<ExpenseEntity> findAllByUser_Id(Integer userId, Pageable pageable);
    List<ExpenseEntity> findAllByUser_IdAndCategory_Id(Integer userId, Integer categoryId);
    Page<ExpenseEntity> findAllByUser_IdAndCategory_Id(Integer userId, Integer categoryId, Pageable pageable);
    List<ExpenseEntity> findAllByUser_IdAndCategory_IdAndAmountGreaterThan(Integer userId, Integer categoryId, BigDecimal goal);
    Page<ExpenseEntity> findAllByUser_IdAndCategory_IdAndAmountGreaterThan(Integer userId, Integer categoryId, BigDecimal goal, Pageable pageable);
    List<ExpenseEntity> findAllByUser_IdAndCategory_IdAndAmountLessThan(Integer userId, Integer categoryId, BigDecimal goal);
    Page<ExpenseEntity> findAllByUser_IdAndCategory_IdAndAmountLessThan(Integer userId, Integer categoryId, BigDecimal goal, Pageable pageable);
    List<ExpenseEntity> findAllByUser_IdAndCategory_IdAndAmountGreaterThanAndDateBetween(Integer userId, Integer categoryId, BigDecimal goal, LocalDate startDate, LocalDate endDate);
    Page<ExpenseEntity> findAllByUser_IdAndCategory_IdAndAmountGreaterThanAndDateBetween(Integer userId, Integer categoryId, BigDecimal goal, LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<ExpenseEntity> findAllByUser_IdAndCategory_IdAndAmountLessThanAndDateBetween(Integer userId, Integer categoryId, BigDecimal goal, LocalDate startDate, LocalDate endDate);
    Page<ExpenseEntity> findAllByUser_IdAndCategory_IdAndAmountLessThanAndDateBetween(Integer userId, Integer categoryId, BigDecimal goal, LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<ExpenseEntity> findAllByUser_IdAndCategory_IdAndDateBetween(Integer userId, Integer categoryId, LocalDate startDate, LocalDate endDate);
    Page<ExpenseEntity> findAllByUser_IdAndCategory_IdAndDateBetween(Integer userId, Integer categoryId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<ExpenseEntity> findAllByUser_IdAndAmountGreaterThan(Integer userId, BigDecimal goal);
    Page<ExpenseEntity> findAllByUser_IdAndAmountGreaterThan(Integer userId, BigDecimal goal, Pageable pageable);
    List<ExpenseEntity> findAllByUser_IdAndAmountLessThan(Integer userId, BigDecimal goal);
    Page<ExpenseEntity> findAllByUser_IdAndAmountLessThan(Integer userId, BigDecimal goal, Pageable pageable);
    List<ExpenseEntity> findAllByUser_IdAndDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);
    Page<ExpenseEntity> findAllByUser_IdAndDateBetween(Integer userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseEntity e WHERE e.user.id = ?1")
    BigDecimal getTotalAmountByUserId(Integer userId);
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseEntity e WHERE e.user.id = ?1 AND e.category.id = ?2")
    BigDecimal getTotalAmountByUserIdAndCategoryId(Integer userId, Integer categoryId);
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseEntity e WHERE e.user.id = ?1 AND e.date BETWEEN ?2 AND ?3")
    BigDecimal getTotalAmountByUserIdAndDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseEntity e WHERE e.user.id = ?1 AND e.category.id = ?2 AND e.date BETWEEN ?3 AND ?4")
    BigDecimal getTotalAmountByUserIdAndCategoryIdAndDateBetween(Integer userId, Integer categoryId, LocalDate startDate, LocalDate endDate);
}
