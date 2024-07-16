package com.projectlyrics.server.domain.auth.authentication.jwt;

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

    private static final String TOKEN_TYPE = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "accessToken";
    private static final String REFRESH_TOKEN_TYPE = "refreshToken";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000L * 14;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 60 * 60 * 24 * 1000L * 14;

    private final Key key;
    private final Clock clock;

    public JwtProvider(
            @Value("${jwt.secret}") String key,
            Clock clock
    ) {
        this.key = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.clock = clock;
    }

    public AuthToken issueTokens(long id, String nickname) {
        JwtClaim jwtClaim = new JwtClaim(id, nickname);
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

    public String issueAccessToken(JwtClaim jwtClaim) {
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
                .claim(TOKEN_TYPE, tokenType)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiredAt))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
