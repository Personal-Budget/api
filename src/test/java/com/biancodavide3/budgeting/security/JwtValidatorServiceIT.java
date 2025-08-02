package com.biancodavide3.budgeting.security;

import com.biancodavide3.budgeting.properties.SupabaseProperties;
import com.biancodavide3.budgeting.security.jwt.SupabaseJwtValidatorService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("prod")
@Disabled
class JwtValidatorServiceIT {

    @Autowired
    private SupabaseJwtValidatorService underTest;
    @Autowired
    private SupabaseProperties supabaseProperties;

    @Test
    void testLoginAndValidateJwt() throws Exception {
        // given
        WebClient webClient = WebClient.create();
        String token = webClient.post()
                .uri(supabaseProperties.getUrl() + "/auth/v1/token?grant_type=password")
                .header("apikey", supabaseProperties.getPublishableKey())
                .header("Content-Type", "application/json")
                .bodyValue(Map.of(
                        "email", supabaseProperties.getTestUserEmail(),
                        "password", supabaseProperties.getTestUserPassword()
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"))
                .block();
        assertThat(token).isNotNull();
        // when
        String userId = underTest.validateTokenAndExtractSubject(token);
        // then
        assertThat(userId).isEqualTo(supabaseProperties.getTestUserId());
    }
}