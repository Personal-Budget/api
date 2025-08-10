package com.biancodavide3.budgeting.db.repositories.specifications;

import com.biancodavide3.budgeting.db.entities.CategoryEntity;
import jakarta.persistence.criteria.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

@Data
@Builder
public class CategorySpecification implements Specification<CategoryEntity> {

    private Long userId;
    private String nameEquals;
    private String nameContains;
    private BigDecimal minGoal;
    private BigDecimal maxGoal;

    @Override
    public Predicate toPredicate(Root<CategoryEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        if (userId != null) {
            predicate = cb.and(predicate, cb.equal(root.get("user").get("id"), userId));
        }

        if (nameEquals != null && !nameEquals.isEmpty()) {
            predicate = cb.and(predicate, cb.equal(cb.lower(root.get("name")), nameEquals.toLowerCase()));
        }

        if (nameContains != null && !nameContains.isEmpty()) {
            predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + nameContains.toLowerCase() + "%"));
        }

        if (minGoal != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("goal"), minGoal));
        }

        if (maxGoal != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("goal"), maxGoal));
        }

        return predicate;
    }
}
