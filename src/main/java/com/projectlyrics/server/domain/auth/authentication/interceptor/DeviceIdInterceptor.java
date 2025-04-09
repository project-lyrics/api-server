package com.projectlyrics.server.domain.auth.authentication.interceptor;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.exception.NotRegisteredDeviceException;
import com.projectlyrics.server.domain.auth.repository.AuthRepository;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;

@Profile({"dev", "prod"})
@Component
@Transactional
@RequiredArgsConstructor
public class DeviceIdInterceptor implements HandlerInterceptor {

    private final AuthRepository authRepository;
    private final UserQueryRepository userQueryRepository;
    private final AuthContext authContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isExcluded(request) && hasNoAuth()) {
            return true;
        }

        if (isOldDevice(request.getHeader("Device-Id"))) {
            throw new NotRegisteredDeviceException();
        }

        return true;
    }

    private boolean isExcluded(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();

        return (requestURI.matches("/api/v1/notes/\\d+") && requestMethod.equalsIgnoreCase(HttpMethod.GET.name()) ||
                requestURI.matches("/api/v1/artists/\\d+") && requestMethod.equalsIgnoreCase(HttpMethod.GET.name()) ||
                requestURI.equals("/api/v1/artists") ||
                requestURI.equals("/api/v1/artists/search") ||
                requestURI.equals("/api/v1/notes/artists") ||
                requestURI.equals("/api/v1/notes/songs") ||
                requestURI.matches("/api/v1/songs/.*") ||
                (requestURI.matches("/api/v1/events") && requestMethod.equalsIgnoreCase(HttpMethod.GET.name()))||
                requestURI.matches("/api/v1/events/refuse") ||
                (requestURI.matches("/api/v1/banners") && requestMethod.equalsIgnoreCase(HttpMethod.GET.name())));
    }

    private boolean hasNoAuth() {
        return authContext.getId() <= 0 &&
                authContext.getNickname() == null;
    }

    private boolean isOldDevice(String deviceId) {
        return userQueryRepository.findById(authContext.getId())
                .map(user -> authRepository.findBySocialIdAndDeviceId(user.getSocialInfo().getSocialId(), deviceId))
                .orElseThrow(() -> new UserNotFoundException())
                .isEmpty();
    }
}
