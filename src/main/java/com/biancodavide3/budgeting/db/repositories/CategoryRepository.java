package com.biancodavide3.budgeting.db.repositories;

import com.biancodavide3.budgeting.db.entities.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/*
CREATE TABLE categories (
                            id SERIAL PRIMARY KEY,
                            user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                            name VARCHAR(100) NOT NULL,
                            goal NUMERIC(10, 2) DEFAULT 0,
                            UNIQUE(user_id, name)
);
 */

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    List<CategoryEntity> findAllByUser_Id(Integer userId);
    Page<CategoryEntity> findAllByUser_Id(Integer userId, Pageable pageable);
    List<CategoryEntity> findAllByUser_IdAndGoalGreaterThan(Integer userId, BigDecimal goal);
    Page<CategoryEntity> findAllByUser_IdAndGoalGreaterThan(Integer userId, BigDecimal goal, Pageable pageable);
    List<CategoryEntity> findAllByUser_IdAndGoalLessThan(Integer userId, BigDecimal goal);
    Page<CategoryEntity> findAllByUser_IdAndGoalLessThan(Integer userId, BigDecimal goal, Pageable pageable);
    Optional<CategoryEntity> findByUser_IdAndName(Integer userId, String name);
}
