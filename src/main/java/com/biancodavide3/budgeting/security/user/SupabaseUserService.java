package com.biancodavide3.budgeting.security.user;

import com.biancodavide3.budgeting.db.entities.UserEntity;
import com.biancodavide3.budgeting.db.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Trust supabase as source of truth for authenticating users. If there isn't a corresponding record
 * in app's db, create it.
 */
@Profile("prod")
@AllArgsConstructor
@Slf4j
@Service
public class SupabaseUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity getUserFromSupabaseId(String subject) {
        UUID supabaseId = UUID.fromString(subject);
        Optional<UserEntity> userOptional = userRepository.findBySupabaseId(supabaseId);
        if (userOptional.isEmpty()) {
            log.warn("User with supabaseId {} not found, creating record in db", supabaseId);
            UserEntity newUser = new UserEntity();
            newUser.setSupabaseId(supabaseId);
            UserEntity savedUser = userRepository.save(newUser);
            log.info("User created with id {}", savedUser.getId());
            return savedUser;
        }
        return userOptional.get();
    }
}
