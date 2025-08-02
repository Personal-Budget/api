package com.biancodavide3.budgeting.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;

import java.text.ParseException;

/**
 * Introduced to run different versions of the application.
 * One talks to supabase to authenticate real users while the other doesn't and just refers
 * to a different database with fake users. When running the fake implementation the tokens sent
 * in bearer must be valid uuids as that is what is stored instead as a supabaseId.
 */
public interface JwtValidatorService {
    String validateTokenAndExtractSubject(String token) throws ParseException, BadJOSEException, JOSEException;
}
