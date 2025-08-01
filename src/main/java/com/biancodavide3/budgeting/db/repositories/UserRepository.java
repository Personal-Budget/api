package com.biancodavide3.budgeting.db.repositories;

import com.biancodavide3.budgeting.db.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findByName(String name);
    Optional<UserEntity> findByEmail(String email);
}
