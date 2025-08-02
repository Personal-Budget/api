package com.biancodavide3.budgeting.security;

import com.biancodavide3.budgeting.properties.SupabaseProperties;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Configuration
public class SecurityConfiguration {
    @Bean
    public DefaultJWTProcessor<SecurityContext> jwtProcessor(SupabaseProperties properties) throws MalformedURLException {
        URL jwksUrl = URI.create(properties.getJwksUrl()).toURL();

        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder
                .create(jwksUrl)
                .build();

        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.ES256, jwkSource);

        DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        processor.setJWSKeySelector(keySelector);
        return processor;
    }
}
