package com.projectlyrics.server.domain.auth.jwt;

import com.projectlyrics.server.domain.auth.authentication.UserAuthentication;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenProvider {

  private static final String MEMBER_ID = "memberId";
  private static final long ACCESS_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000L * 14;
  private static final long REFRESH_TOKEN_EXPIRATION_TIME = 60 * 60 * 24 * 1000L * 14;

  @Value("${jwt.secret}")
  private String JWT_SECRET;

  public AuthToken issueTokens(long id) {
    UserAuthentication authentication = UserAuthentication.of(id);

    return new AuthToken(
        issueAccessToken(authentication),
        issueRefreshToken(authentication)
    );
  }

  private String issueAccessToken(final Authentication authentication) {
    return issueToken(authentication, ACCESS_TOKEN_EXPIRATION_TIME);
  }


  private String issueRefreshToken(final Authentication authentication) {
    return issueToken(authentication, REFRESH_TOKEN_EXPIRATION_TIME);
  }

  private String issueToken(final Authentication authentication, final Long expiredTime) {
    return Jwts.builder()
        .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // Header
        .setClaims(generateClaims(authentication)) // Claim
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
        .signWith(getSigningKey()) // Signature
        .compact();
  }

  private static Claims generateClaims(Authentication authentication) {
    Claims claims = Jwts.claims();
    claims.put(MEMBER_ID, authentication.getPrincipal());

    return claims;
  }

  private SecretKey getSigningKey() {
    String encodedKey = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes()); //SecretKey 통해 서명 생성
    return Keys.hmacShaKeyFor(encodedKey.getBytes());   //일반적으로 HMAC (Hash-based Message Authentication Code) 알고리즘 사용
  }

  public JwtValidationType validateToken(String token) {
    try {
      getBody(token);

      return JwtValidationType.VALID_JWT;
    } catch (MalformedJwtException ex) {
      return JwtValidationType.INVALID_JWT_TOKEN;
    } catch (ExpiredJwtException ex) {
      return JwtValidationType.EXPIRED_JWT_TOKEN;
    } catch (UnsupportedJwtException ex) {
      return JwtValidationType.UNSUPPORTED_JWT_TOKEN;
    } catch (IllegalArgumentException ex) {
      return JwtValidationType.EMPTY_JWT;
    }
  }

  private Claims getBody(final String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public Long getUserFromJwt(String token) {
    Claims claims = getBody(token);
    return Long.valueOf(claims.get(MEMBER_ID).toString());
  }
}
