package com.biancodavide3.budgeting.security;

import com.biancodavide3.budgeting.db.entities.UserEntity;
import com.biancodavide3.budgeting.db.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService underTest;

    @Mock
    private Jwt jwt; // Mock Jwt

    @Test
    void itShouldGetUserFromSupabaseIdExistsInDb() {
        // given
        UUID supabaseId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .supabaseId(supabaseId)
                .build();
        given(userRepository.findBySupabaseId(supabaseId)).willReturn(Optional.of(userEntity));
        given(jwt.getSubject()).willReturn(supabaseId.toString());

        // when
        UserDetails userDetails = underTest.getUserDetails(jwt);

        // then
        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
        UserEntity entity = ((CustomUserDetails) userDetails).getUser();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getSupabaseId()).isEqualTo(supabaseId);
    }

    @Test
    void itShouldGetUserFromSupabaseIdNewUser() {
        // given
        UUID supabaseId = UUID.randomUUID();
        given(userRepository.findBySupabaseId(supabaseId)).willReturn(Optional.empty());
        UserEntity savedUser = UserEntity.builder()
                .id(1L)
                .supabaseId(supabaseId)
                .build();
        given(userRepository.save(any(UserEntity.class))).willReturn(savedUser);
        given(jwt.getSubject()).willReturn(supabaseId.toString());

        // when
        UserDetails userDetails = underTest.getUserDetails(jwt);

        // then
        verify(userRepository).save(any(UserEntity.class));
        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
        UserEntity entity = ((CustomUserDetails) userDetails).getUser();
        assertThat(entity.getSupabaseId()).isEqualTo(supabaseId);
    }
}
