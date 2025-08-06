package com.biancodavide3.budgeting.db.repositories;

import com.biancodavide3.budgeting.db.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findBySupabaseId(UUID supabaseId);
}
