package com.projectlyrics.server.domain.user.authentication;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.TokenExtractor;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtClaim;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtExtractor;
import com.projectlyrics.server.domain.user.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final AuthContext authContext;
    private final JwtExtractor jwtExtractor;
    private final TokenExtractor tokenExtractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isExcluded((request))) {
            return true;
        }

        setAuthContext(request);
        if (Role.ADMIN.equals(authContext.getRole())) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
    private boolean isExcluded(HttpServletRequest request) {
        return request.getRequestURI().matches("/api/v1/artists") &&
                request.getMethod().equalsIgnoreCase(HttpMethod.GET.name()) ||
                request.getRequestURI().matches("/api/v1/artists/\\d+") &&
                request.getMethod().equalsIgnoreCase(HttpMethod.GET.name());
    }

    private void setAuthContext(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        JwtClaim claim = jwtExtractor.parseJwtClaim(tokenExtractor.extractToken(authorization));
        authContext.setRole(claim.role());
    }
}
