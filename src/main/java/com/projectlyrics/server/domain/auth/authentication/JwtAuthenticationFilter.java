package com.projectlyrics.server.domain.auth.authentication;

import static com.projectlyrics.server.domain.auth.jwt.JwtValidationType.VALID_JWT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.global.exception.JwtValidationException;
import com.projectlyrics.server.global.message.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String TOKEN_PREFIX = "Bearer ";
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = getAccessTokenFromRequest(request);

      if (isValidToken(token))
        setUserIntoContext(token, request);
    } catch (Exception e) {
      throw new JwtValidationException(ErrorCode.INVALID_TOKEN);
    }

    filterChain.doFilter(request, response);
  }

  private String getAccessTokenFromRequest(HttpServletRequest request) {
    return isContainsAccessToken(request) ? getAuthorizationAccessToken(request) : null;
  }

  private boolean isContainsAccessToken(HttpServletRequest request) {
    val authorization = request.getHeader(AUTHORIZATION);
    return authorization != null && authorization.startsWith(TOKEN_PREFIX);
  }

  private String getAuthorizationAccessToken(HttpServletRequest request) {
    return request.getHeader(AUTHORIZATION)
        .substring(TOKEN_PREFIX.length());
  }

  private boolean isValidToken(String token) {
    return jwtTokenProvider.validateToken(token) == VALID_JWT;
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
