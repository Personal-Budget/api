package com.biancodavide3.budgeting.security.jwt;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Fake implementation returns the token as a supabaseId without processing.
 * The token sent with the request needs to be an actual uuid in order to mirror production perfectly.
 */
@Service
@Profile("dev")
public class MockJwtValidatorService implements JwtValidatorService {
    @Override
    public String validateTokenAndExtractSubject(String token) {
        return token;
    }
}
