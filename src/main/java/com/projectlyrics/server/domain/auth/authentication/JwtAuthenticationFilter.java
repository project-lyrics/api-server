package com.projectlyrics.server.domain.auth.authentication;

import static com.projectlyrics.server.domain.auth.jwt.JwtValidationType.EXPIRED_JWT_TOKEN;
import static com.projectlyrics.server.domain.auth.jwt.JwtValidationType.VALID_JWT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.projectlyrics.server.domain.auth.exception.TokenExpiredException;
import com.projectlyrics.server.domain.auth.exception.InvalidTokenException;
import com.projectlyrics.server.domain.auth.exception.WrongTokenTypeException;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.jwt.JwtValidationType;
import com.projectlyrics.server.global.handler.FilterExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final FilterExceptionHandler filterExceptionHandler;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return super.shouldNotFilter(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getAccessTokenFromRequest(request);
            validateToken(token);
            setUserIntoContext(token, request);
        } catch (RuntimeException e) {
            filterExceptionHandler.handleFilterException(e, response);
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION))
                .filter(t -> t.startsWith(TOKEN_PREFIX))
                .map(t -> t.replace(TOKEN_PREFIX, ""))
                .orElseThrow(WrongTokenTypeException::new);
    }

    private void validateToken(String token) {
        JwtValidationType validationResult = jwtTokenProvider.validateToken(token);

        if (validationResult == VALID_JWT)
            return;

        if (validationResult == EXPIRED_JWT_TOKEN)
            throw new TokenExpiredException();

        throw new InvalidTokenException();
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
