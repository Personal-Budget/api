package com.biancodavide3.budgeting.db.repositories.specifications;

import com.biancodavide3.budgeting.db.entities.UserEntity;
import jakarta.persistence.criteria.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
@Builder
public class UserSpecification implements Specification<UserEntity> {

    private String nameContains;
    private String emailContains;

    @Override
    public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();

        if (nameContains != null && !nameContains.isEmpty()) {
            predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + nameContains.toLowerCase() + "%"));
        }

        if (emailContains != null && !emailContains.isEmpty()) {
            predicate = cb.and(predicate, cb.like(cb.lower(root.get("email")), "%" + emailContains.toLowerCase() + "%"));
        }

        return predicate;
    }
}

