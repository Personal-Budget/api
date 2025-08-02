package com.biancodavide3.budgeting.security.user;

import com.biancodavide3.budgeting.db.entities.UserEntity;
import com.biancodavide3.budgeting.db.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Trust supabase as source of truth for authenticating users. If there isn't a corresponding record
 * in app's db, create it. In the real implementation there are supabaseId - userId couples that reflect real users.
 * In the fake implementation what is stored is the couple token - userId but note that we still must comply
 * to database schema so the token as to be a valid uuid.
 */

@AllArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

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
