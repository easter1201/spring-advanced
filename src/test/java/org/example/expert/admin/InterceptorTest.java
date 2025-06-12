package org.example.expert.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.expert.config.AdminInterceptor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.service.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterceptorTest {
    @InjectMocks
    private AdminInterceptor adminInterceptor;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;

    @Test
    void access_who_admin() throws Exception{
        //given
        AuthUser authUser = new AuthUser(1L, "admin@example.com", UserRole.ADMIN);
        when(request.getSession()).thenReturn(session);
        when(request.getSession().getAttribute("authUser")).thenReturn(authUser);
        when(request.getRequestURI()).thenReturn("/admin/comments/1");

        //when
        boolean result = adminInterceptor.preHandle(request, response, new Object());

        //then
        assertTrue(result);
    }

    @Test
    void block_who_not_admin() throws Exception{
        //given
        AuthUser authUser = new AuthUser(2L, "user@example.com", UserRole.USER);
        when(request.getSession()).thenReturn(session);
        when(request.getSession().getAttribute("authUser")).thenReturn(authUser);
        //when(request.getRequestURI()).thenReturn("/admin/comments/1");

        //when
        boolean result = adminInterceptor.preHandle(request, response, new Object());

        //then
        assertFalse(result);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "access denied");
    }
}
