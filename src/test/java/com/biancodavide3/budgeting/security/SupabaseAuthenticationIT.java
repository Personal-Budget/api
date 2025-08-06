package com.biancodavide3.budgeting.security;

import com.biancodavide3.budgeting.properties.SupabaseProperties;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("prod")
@Disabled
@AutoConfigureMockMvc
class SupabaseAuthenticationIT {

    @Autowired
    private MockMvc mockMvc;
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
        String meResponse = mockMvc.perform(get("/me")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        // then
        assertThat(meResponse).contains(supabaseProperties.getTestUserId());
        System.out.println(meResponse);
    }
}