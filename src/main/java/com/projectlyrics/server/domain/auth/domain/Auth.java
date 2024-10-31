package com.projectlyrics.server.domain.auth.domain;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "auth", timeToLive = 60 * 60 * 24 * 1000L * 14)
public class Auth {

    @Id
    private String socialId;
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;
    @Indexed
    private String refreshToken;

    private Auth(
            String socialId,
            AuthProvider authProvider,
            String refreshToken
    ) {
        this.socialId = socialId;
        this.authProvider = authProvider;
        this.refreshToken = refreshToken;
    }

    public static Auth create(SocialInfo socialInfo, String refreshToken) {
        return new Auth(
                socialInfo.getSocialId(),
                socialInfo.getAuthProvider(),
                refreshToken
        );
    }
}
