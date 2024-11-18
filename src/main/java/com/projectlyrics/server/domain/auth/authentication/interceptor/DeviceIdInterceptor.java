package com.projectlyrics.server.domain.auth.authentication.interceptor;

import com.projectlyrics.server.domain.auth.exception.NotRegisteredDeviceException;
import com.projectlyrics.server.domain.auth.repository.AuthRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;

@Profile({"dev", "prod"})
@Component
@Transactional
@RequiredArgsConstructor
public class DeviceIdInterceptor implements HandlerInterceptor {

    private final AuthRepository authRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (included(request)) {
            authRepository.findByDeviceId(request.getHeader("Device-Id"))
                    .orElseThrow(NotRegisteredDeviceException::new);
        }

        return true;
    }

    private boolean included(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return !requestURI.matches("/api/v1/auth/sign-in") &&
                !requestURI.matches("/api/v1/auth/sign-up");
    }
}
