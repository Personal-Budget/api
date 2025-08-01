package com.biancodavide3.budgeting.db.repositories.specifications;

import com.biancodavide3.budgeting.db.entities.BudgetEntity;
import jakarta.persistence.criteria.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@Builder
public class BudgetSpecification implements Specification<BudgetEntity> {

    private Integer userId;
    private YearMonth month;
    private BigDecimal minTotalBudget;
    private BigDecimal maxTotalBudget;

    @Override
    public Predicate toPredicate(Root<BudgetEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        if (userId != null) {
            predicate = cb.and(predicate, cb.equal(root.get("user").get("id"), userId));
        }

        if (month != null) {
            predicate = cb.and(predicate, cb.equal(root.get("month"), month));
        }

        if (minTotalBudget != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("totalBudget"), minTotalBudget));
        }

        if (maxTotalBudget != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("totalBudget"), maxTotalBudget));
        }

        return predicate;
    }
}
