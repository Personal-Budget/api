package com.biancodavide3.budgeting.security.user;

import com.biancodavide3.budgeting.db.entities.UserEntity;
import com.biancodavide3.budgeting.db.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void itShouldGetUserFromSupabaseIdExistsInDb() {
        // given
        UUID supabaseId = UUID.randomUUID();
        String subject = supabaseId.toString();
        Long userId = 1L;
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .supabaseId(supabaseId)
                .build();
        Optional<UserEntity> optional = Optional.of(userEntity);
        given(userRepository.findBySupabaseId(supabaseId)).willReturn(optional);
        // when
        UserEntity userFromSupabaseId = underTest.getUserFromSupabaseId(subject);
        // then
        assertThat(userFromSupabaseId.getId()).isEqualTo(userId);
        assertThat(userFromSupabaseId.getSupabaseId()).isEqualTo(supabaseId);
    }

    @Test
    void itShouldGetUserFromSupabaseIdNewUser() {
        // given
        UUID supabaseId = UUID.randomUUID();
        String subject = supabaseId.toString();
        given(userRepository.findBySupabaseId(supabaseId)).willReturn(Optional.empty());
        UserEntity savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setSupabaseId(supabaseId);
        given(userRepository.save(any(UserEntity.class))).willReturn(savedUser);
        // when
        UserEntity result = underTest.getUserFromSupabaseId(subject);
        // then
        verify(userRepository).save(any(UserEntity.class));
        assertThat(result.getSupabaseId()).isEqualTo(supabaseId);
    }
}