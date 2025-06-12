package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.service.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.service.dto.response.UserResponse;
import org.example.expert.domain.user.service.entity.User;
import org.example.expert.domain.user.service.enums.UserRole;
import org.example.expert.domain.user.service.repository.UserRepository;
import org.example.expert.domain.user.service.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void exception_when_no_user(){
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> {userService.getUser(1L);});

        //then
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void success_user_search(){
        //given
        User user = new User("email@email.com", "password1234", UserRole.USER);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        //when
        UserResponse response = userService.getUser(1L);

        //then
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getId(), response.getId());
    }

    @Test
    void exception_when_cant_find_user(){
        //given
        UserChangePasswordRequest request = new UserChangePasswordRequest("oldpassword", "newPassword");
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> {userService.changePassword(1L, request);});

        //then
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void exception_when_same(){
        //given
        User user = new User("email@email.com", "encodedPassword", UserRole.USER);
        UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword", "newPassword");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("newPassword", "encodedPassword")).willReturn(true);

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> {userService.changePassword(1L, request);});

        //then
        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }

    @Test
    void exception_when_wrong(){
        //given
        User user = new User("email@email.com", "encodedPassword", UserRole.USER);
        UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword", "newPassword");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("newPassword", "encodedPassword")).willReturn(false);
        given(passwordEncoder.matches("oldPassword", "encodedPassword")).willReturn(false);

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> {userService.changePassword(1L, request);});

        //then
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    void success_change_password(){
        //given
        User user = new User("email@email.com", "encodedPassword", UserRole.USER);
        UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword", "newPassword");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("newPassword", "encodedPassword")).willReturn(false);
        given(passwordEncoder.matches("oldPassword", "encodedPassword")).willReturn(true);
        given(passwordEncoder.encode("newPassword")).willReturn("newEncodedPassword");

        //when
        userService.changePassword(1L, request);

        //then
        assertEquals("newEncodedPassword", user.getPassword());
    }
}
