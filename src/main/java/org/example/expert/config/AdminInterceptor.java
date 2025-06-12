package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.service.enums.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        AuthUser authUser = (AuthUser) request.getSession().getAttribute("authUser");
        //유저 인증여부 확인
        if(authUser == null || authUser.getUserRole() != UserRole.ADMIN){
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "access denied");
            return false;
        }
        //admin 접속 시 해당 admin의 userId, 접속시간, url log로 남김
        log.info("Access Admin : userId={}, time={}, url={}",
                authUser.getId(),
                System.currentTimeMillis(),
                request.getRequestURI());
        return true;
    }
}
