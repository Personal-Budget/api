package com.biancodavide3.budgeting.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
@AllArgsConstructor
public class SupabaseJwtValidator {
    private final DefaultJWTProcessor<SecurityContext> jwtProcessor;

    public String validateAndExtractUserId(String token) throws ParseException, BadJOSEException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        jwtProcessor.process(signedJWT, null);
        return signedJWT.getJWTClaimsSet().getSubject();
    }
}
