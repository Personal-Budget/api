package com.biancodavide3.budgeting.security;

import com.biancodavide3.budgeting.db.entities.UserEntity;
import com.biancodavide3.budgeting.db.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Returns the UserDetails based on the JWT token.
 * Trust supabase as source of truth for authenticating users.
 * If there isn't a corresponding record in app's db, create it.
 */
@AllArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserDetails getUserDetails(Jwt jwt) {
        UUID supabaseId = UUID.fromString(jwt.getSubject());
        Optional<UserEntity> userOptional = userRepository.findBySupabaseId(supabaseId);
        UserEntity user = userOptional.orElseGet(() -> {
            log.info("User with supabaseId {} not found, creating record in db", supabaseId);
            // eventually add more stuff like roles for custom features etc.
            UserEntity newUser = UserEntity.builder()
                            .supabaseId(supabaseId)
                            .build();
            return userRepository.save(newUser);
        });
        return new CustomUserDetails(user);
    }
}
