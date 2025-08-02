package com.biancodavide3.budgeting.db.repositories;

import com.biancodavide3.budgeting.db.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/*
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       supabase_id UUID NOT NULL,
);
 */

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findBySupabaseId(UUID supabaseId);
}
