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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isExcluded(request)) {
            setAuthContextIfAvailable(request);
            return true;
        }

        setAuthContext(request);
        return true;
    }

    private boolean isExcluded(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();

        return ((requestURI.matches("/api/v1/notes/\\d+") && requestMethod.equalsIgnoreCase(HttpMethod.GET.name())) ||
                (requestURI.matches("/api/v1/artists/\\d+") && requestMethod.equalsIgnoreCase(HttpMethod.GET.name())) ||
                requestURI.equals("/api/v1/artists") ||
                requestURI.equals("/api/v1/artists/search") ||
                requestURI.equals("/api/v1/notes/artists") ||
                requestURI.equals("/api/v1/notes/songs") ||
                requestURI.matches("/api/v1/songs/.*") ||
                (requestURI.matches("/api/v1/events") && requestMethod.equalsIgnoreCase(HttpMethod.GET.name())) );
    }

    private void setAuthContext(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        JwtClaim claim = jwtExtractor.parseJwtClaim(tokenExtractor.extractToken(authorization));
        authContext.setNickname(claim.nickname());
        authContext.setId(claim.id());
    }

    private void setAuthContextIfAvailable(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null) {
            String token = tokenExtractor.extractToken(authorization);

            if (token != null) {
                JwtClaim claim = jwtExtractor.parseJwtClaim(token);

                if (claim != null) {
                    authContext.setNickname(claim.nickname());
                    authContext.setId(claim.id());
                }
            }
        }
    }
}
