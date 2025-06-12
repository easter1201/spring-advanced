package org.example.expert.domain.user.service;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.service.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.entity.User;
import org.example.expert.domain.user.service.enums.UserRole;
import org.example.expert.domain.user.service.repository.UserRepository;
import org.example.expert.domain.user.service.service.UserAdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class UserAdminserviceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    void exception_when_cant_find_user(){
        //given
        UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userAdminService.changeUserRole(1L, request));

        //then
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void change_user_role(){
        User user = new User("email.example.com", "password", UserRole.USER);
        UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        //when
        userAdminService.changeUserRole(1L, request);

        //then
        assertEquals(UserRole.ADMIN, user.getUserRole());
    }
}
