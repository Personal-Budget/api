package com.biancodavide3.budgeting.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.text.ParseException;

/**
 * Uses the jwtProcessor configured to validate the incoming token and retrieve the subject associated to it.
 */
@Service
@AllArgsConstructor
@Profile("prod")
public class SupabaseJwtValidatorService implements JwtValidatorService {
    private final DefaultJWTProcessor<SecurityContext> jwtProcessor;

    @Override
    public String validateTokenAndExtractSubject(String token) throws ParseException, BadJOSEException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        jwtProcessor.process(signedJWT, null);
        return signedJWT.getJWTClaimsSet().getSubject();
    }
}
