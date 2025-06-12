package org.example.expert.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.user.service.controller.UserAdminController;
import org.example.expert.domain.user.service.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.enums.UserRole;
import org.example.expert.domain.user.service.service.UserAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserAdminService userAdminService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;

    private String token;

    @BeforeEach
    void setUp(){
        token = jwtUtil.createToken(1L, "admin@email.com", UserRole.ADMIN);
    }
    @Test
    void success_request() throws Exception{
        //given
        UserRoleChangeRequest request = new UserRoleChangeRequest(UserRole.ADMIN.name());
        String json = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(patch("/admin/users/{userId}", 1L)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        //then
        verify(userAdminService).changeUserRole(1L, request);
    }
}
