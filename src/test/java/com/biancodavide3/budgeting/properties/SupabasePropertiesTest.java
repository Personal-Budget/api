package com.biancodavide3.budgeting.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class SupabasePropertiesTest {

    @Autowired
    private SupabaseProperties underTest;

    @Test
    void itShouldLoadSupabasePropertiesCorrectly() {
        assertThat(underTest.getUrl()).isNotEmpty();
        assertThat(underTest.getSecretKey()).isNotEmpty();
        assertThat(underTest.getJwksUrl()).isNotEmpty();
        assertThat(underTest.getPublishableKey()).isNotEmpty();
    }
}