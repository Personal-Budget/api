package com.biancodavide3.budgeting.db.repositories.specifications;

import com.biancodavide3.budgeting.db.entities.BudgetCategoryEntity;
import jakarta.persistence.criteria.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

@Data
@Builder
public class BudgetCategorySpecification implements Specification<BudgetCategoryEntity> {

    private Long budgetId;
    private Long categoryId;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    @Override
    public Predicate toPredicate(Root<BudgetCategoryEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        if (budgetId != null) {
            predicate = cb.and(predicate, cb.equal(root.get("budget").get("id"), budgetId));
        }

        if (categoryId != null) {
            predicate = cb.and(predicate, cb.equal(root.get("category").get("id"), categoryId));
        }

        if (minAmount != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
        }

        if (maxAmount != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
        }

        return predicate;
    }
}
