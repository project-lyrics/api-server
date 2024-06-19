package com.projectlyrics.server.domain.auth.authentication;

import static com.projectlyrics.server.domain.auth.jwt.JwtValidationType.EXPIRED_JWT_TOKEN;
import static com.projectlyrics.server.domain.auth.jwt.JwtValidationType.VALID_JWT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.projectlyrics.server.domain.auth.exception.AccessTokenExpiredException;
import com.projectlyrics.server.domain.auth.exception.WrongTokenTypeException;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.jwt.JwtValidationType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getAccessTokenFromRequest(request);

            if (isValidToken(token, response)) {
                setUserIntoContext(token, request);
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION))
                .filter(t -> t.startsWith(TOKEN_PREFIX))
                .map(t -> t.replace(TOKEN_PREFIX, ""))
                .orElseThrow(WrongTokenTypeException::new);
    }

    private boolean isValidToken(String token, HttpServletResponse response) {
        JwtValidationType validationResult = jwtTokenProvider.validateToken(token);

        if (validationResult == EXPIRED_JWT_TOKEN) {
            throw new AccessTokenExpiredException();
        }

        return validationResult == VALID_JWT;
    }

    private void setUserIntoContext(String token, HttpServletRequest request) {
        UserAuthentication authentication = UserAuthentication.of(getUserId(token));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private long getUserId(String token) {
        return jwtTokenProvider.getUserIdFromJwt(token);
    }
}
