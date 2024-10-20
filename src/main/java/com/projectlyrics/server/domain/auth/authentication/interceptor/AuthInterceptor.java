package com.projectlyrics.server.domain.auth.authentication.interceptor;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.TokenExtractor;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtClaim;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthContext authContext;
    private final TokenExtractor tokenExtractor;
    private final JwtExtractor jwtExtractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isExcluded(request)) {
            return true;
        }

        setAuthContext(request);
        return true;
    }

    private boolean isExcluded(HttpServletRequest request) {
        return request.getRequestURI().matches("/api/v1/notes/\\d+") &&
                request.getMethod().equalsIgnoreCase(HttpMethod.GET.name());
    }

    private void setAuthContext(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        JwtClaim claim = jwtExtractor.parseJwtClaim(tokenExtractor.extractToken(authorization));
        authContext.setNickname(claim.nickname());
        authContext.setId(claim.id());
    }
}
