package com.projectlyrics.server.domain.auth.authentication;

import static com.projectlyrics.server.domain.auth.jwt.JwtValidationType.EXPIRED_JWT_TOKEN;
import static com.projectlyrics.server.domain.auth.jwt.JwtValidationType.VALID_JWT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.projectlyrics.server.domain.auth.exception.InvalidTokenException;
import com.projectlyrics.server.domain.auth.exception.TokenExpiredException;
import com.projectlyrics.server.domain.auth.exception.WrongTokenTypeException;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.jwt.JwtValidationType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";

    @Value("#{'${auth.free-apis}'.split(',')}")
    private String[] excludePath;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher uriMatcher = new AntPathMatcher();
        String uri = request.getRequestURI();

        return Arrays.stream(excludePath)
                .anyMatch(api -> uriMatcher.match(api, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getAccessTokenFromRequest(request);

        validateToken(token);

        setUserIntoContext(token, request);
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

        if (validationResult.equals(VALID_JWT))
            return;

        if (validationResult.equals(EXPIRED_JWT_TOKEN))
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
