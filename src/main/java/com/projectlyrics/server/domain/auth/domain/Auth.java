package com.projectlyrics.server.domain.auth.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("auth")
public class Auth {

    @Id
    private String socialId;
    @Indexed
    private String refreshToken;

    private Auth(
            String socialId,
            String refreshToken
    ) {
        this.socialId = socialId;
        this.refreshToken = refreshToken;
    }

    public static Auth create(String socialId, String refreshToken) {
        return new Auth(
                socialId,
                refreshToken
        );
    }
}
