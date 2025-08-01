package com.biancodavide3.budgeting.db.repositories.specifications;

import com.biancodavide3.budgeting.db.entities.ExpenseEntity;
import jakarta.persistence.criteria.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ExpenseSpecification implements Specification<ExpenseEntity> {

    private Long userId;
    private Long categoryId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String descriptionContains;

    @Override
    public Predicate toPredicate(Root<ExpenseEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        if (userId != null) {
            predicate = cb.and(predicate, cb.equal(root.get("user").get("id"), userId));
        }

        if (categoryId != null) {
            predicate = cb.and(predicate, cb.equal(root.get("category").get("id"), categoryId));
        }

        if (dateFrom != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("date"), dateFrom));
        }

        if (dateTo != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("date"), dateTo));
        }

        if (minAmount != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
        }

        if (maxAmount != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
        }

        if (descriptionContains != null && !descriptionContains.isEmpty()) {
            predicate = cb.and(predicate, cb.like(cb.lower(root.get("description")), "%" + descriptionContains.toLowerCase() + "%"));
        }

        return predicate;
    }
}
