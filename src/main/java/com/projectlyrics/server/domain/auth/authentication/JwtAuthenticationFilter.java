package com.projectlyrics.server.domain.auth.authentication;

import static com.projectlyrics.server.domain.auth.jwt.JwtValidationType.VALID_JWT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.JwtValidationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String TOKEN_PREFIX = "Bearer ";

  @Value("#{'${auth.free-apis}'.split(',')}")
  private String[] authFreeApis;

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = new UrlPathHelper().getPathWithinApplication(request);
    return Arrays.stream(authFreeApis)
        .anyMatch(pattern -> new AntPathMatcher().match(pattern, path));
  }

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = getAccessTokenFromRequest(request);

      if (isValidToken(token)) {
        setUserIntoContext(token, request);
      }
    } catch (Exception e) {
      throw new JwtValidationException(ErrorCode.INVALID_TOKEN);
    }

    filterChain.doFilter(request, response);
  }

  private String getAccessTokenFromRequest(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(AUTHORIZATION))
        .filter(t -> t.startsWith(TOKEN_PREFIX))
        .orElseThrow(() -> new JwtValidationException(ErrorCode.WRONG_TOKEN_TYPE));
  }

  private boolean isValidToken(String token) {
    return jwtTokenProvider.validateToken(token).jwtValidationType() == VALID_JWT;
  }

  private void setUserIntoContext(String token, HttpServletRequest request) {
    UserAuthentication authentication = UserAuthentication.of(getUserId(token));
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private long getUserId(String token) {
    return jwtTokenProvider.getUserFromJwt(token);
  }
}
