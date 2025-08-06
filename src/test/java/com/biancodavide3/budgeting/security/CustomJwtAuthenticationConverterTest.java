// src/test/java/com/biancodavide3/budgeting/security/CustomJwtAuthenticationConverterTest.java
package com.biancodavide3.budgeting.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomJwtAuthenticationConverterTest {

    @Mock
    private UserService userService;

    @Mock
    private Jwt jwt;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private CustomJwtAuthenticationConverter converter;

    @Test
    void convert_shouldReturnUsernamePasswordAuthenticationTokenWithUserDetails() {
        // given
        given(userService.getUserDetails(jwt)).willReturn(userDetails);
        // when
        AbstractAuthenticationToken token = converter.convert(jwt);
        // then
        assertThat(token).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(token.getPrincipal()).isEqualTo(userDetails);
        assertThat(token.getCredentials()).isEqualTo(jwt);
        assertThat(token.getAuthorities()).isEqualTo(userDetails.getAuthorities());
    }
}