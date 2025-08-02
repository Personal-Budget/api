package com.biancodavide3.budgeting.security.user;

import com.biancodavide3.budgeting.db.entities.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("dev")
@Service
public class MockUserService implements UserService {
    @Override
    public UserEntity getUserFromSupabaseId(String supabaseId) {
        log.info("Using MockUserService to return a fake user");
        return null;
    }
}
