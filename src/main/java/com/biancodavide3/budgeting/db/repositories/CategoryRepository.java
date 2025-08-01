package com.biancodavide3.budgeting.db.repositories;

import com.biancodavide3.budgeting.db.entities.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/*
CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                            name VARCHAR(100) NOT NULL,
                            goal NUMERIC(10, 2) DEFAULT 0,
                            UNIQUE(user_id, name)
);
 */

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>, JpaSpecificationExecutor<CategoryEntity> {
    List<CategoryEntity> findAllByUser_Id(Long userId);
    Page<CategoryEntity> findAllByUser_Id(Long userId, Pageable pageable);
    List<CategoryEntity> findAllByUser_IdAndGoalGreaterThan(Long userId, BigDecimal goal);
    Page<CategoryEntity> findAllByUser_IdAndGoalGreaterThan(Long userId, BigDecimal goal, Pageable pageable);
    List<CategoryEntity> findAllByUser_IdAndGoalLessThan(Long userId, BigDecimal goal);
    Page<CategoryEntity> findAllByUser_IdAndGoalLessThan(Long userId, BigDecimal goal, Pageable pageable);
    Optional<CategoryEntity> findByUser_IdAndName(Long userId, String name);
}
