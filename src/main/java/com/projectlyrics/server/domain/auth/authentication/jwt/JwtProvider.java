package com.projectlyrics.server.domain.auth.authentication.jwt;

import com.projectlyrics.server.domain.user.entity.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtProvider {

    private final String TOKEN_TYPE;
    private final String ACCESS_TOKEN_TYPE;
    private final String REFRESH_TOKEN_TYPE;
    private final long ACCESS_TOKEN_EXPIRATION_TIME;
    private final long REFRESH_TOKEN_EXPIRATION_TIME;
    private final Key key;
    private final Clock clock;

    public JwtProvider(
            @Value("${jwt.token.type}") String tokenType,
            @Value("${jwt.token.access-token-type}") String accessTokenType,
            @Value("${jwt.token.refresh-token-type}") String refreshTokenType,
            @Value("${jwt.token.expiration.access-token}") long accessTokenExpirationTime,
            @Value("${jwt.token.expiration.refresh-token}") long refreshTokenExpirationTime,
            @Value("${jwt.secret}") String secretKey,
            Clock clock
    ) {
        this.TOKEN_TYPE = tokenType;
        this.ACCESS_TOKEN_TYPE = accessTokenType;
        this.REFRESH_TOKEN_TYPE = refreshTokenType;
        this.ACCESS_TOKEN_EXPIRATION_TIME = accessTokenExpirationTime;
        this.REFRESH_TOKEN_EXPIRATION_TIME = refreshTokenExpirationTime;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.clock = clock;
    }

    public AuthToken issueTokens(long id, String nickname, Role role) {
        JwtClaim jwtClaim = new JwtClaim(id, nickname, role);

        return new AuthToken(
                issueAccessToken(jwtClaim),
                issueRefreshToken(jwtClaim)
        );
    }

    public AuthToken issueTokens(JwtClaim jwtClaim) {
        return new AuthToken(
                issueAccessToken(jwtClaim),
                issueRefreshToken(jwtClaim)
        );
    }

    private String issueAccessToken(JwtClaim jwtClaim) {
        return issueToken(jwtClaim, ACCESS_TOKEN_EXPIRATION_TIME, ACCESS_TOKEN_TYPE);
    }


    private String issueRefreshToken(JwtClaim jwtClaim) {
        return issueToken(jwtClaim, REFRESH_TOKEN_EXPIRATION_TIME, REFRESH_TOKEN_TYPE);
    }

    private String issueToken(JwtClaim claim, long expiration, String tokenType) {
        Instant now = clock.instant();
        Instant expiredAt = now.plus(expiration, ChronoUnit.MILLIS);

        return Jwts.builder()
                .claim(JwtClaim.USER_ID, claim.id())
                .claim(JwtClaim.NICKNAME, claim.nickname())
                .claim(JwtClaim.ROLE, claim.role().name())
                .claim(TOKEN_TYPE, tokenType)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiredAt))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
