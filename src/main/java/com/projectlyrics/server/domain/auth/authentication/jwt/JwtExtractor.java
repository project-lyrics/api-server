package com.projectlyrics.server.domain.auth.authentication.jwt;

import com.projectlyrics.server.domain.auth.exception.InvalidTokenException;
import com.projectlyrics.server.domain.auth.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtExtractor {

    private final Key key;

    public JwtExtractor(
            @Value("${jwt.secret}") String key
    ) {
        this.key = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public JwtClaim parseJwtClaim(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build();

            Claims claims = parser.parseClaimsJws(token)
                    .getBody();
            return new JwtClaim(Long.parseLong(String.valueOf(claims.get(JwtClaim.USER_ID))), String.valueOf(claims.get(JwtClaim.NICKNAME)));
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }
}