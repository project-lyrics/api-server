package com.projectlyrics.server.domain.auth.authentication;

import com.projectlyrics.server.domain.auth.authentication.jwt.JwtClaim;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        JwtClaim claim = jwtExtractor.parseJwtClaim(tokenExtractor.extractToken(authorization));
        authContext.setNickname(claim.nickname());
        authContext.setId(claim.id());

        return true;
    }
}
