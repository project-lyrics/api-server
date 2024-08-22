package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.auth.service.social.apple.dto.AppleUserInfo;
import com.projectlyrics.server.domain.auth.service.social.kakao.dto.KakaoUserInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkEnum;
import static com.projectlyrics.server.domain.common.util.DomainUtils.checkString;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialInfo {

    @Enumerated(value = EnumType.STRING)
    private AuthProvider authProvider;

    @Column(nullable = false)
    private String socialId;

    public static SocialInfo of(AuthProvider authProvider, String socialId) {
        checkEnum(authProvider);
        checkString(socialId);

        return new SocialInfo(
                authProvider,
                socialId
        );
    }

    public static SocialInfo from(AppleUserInfo appleUserInfo) {
        return of(
                AuthProvider.APPLE,
                appleUserInfo.id()
        );
    }

    public static SocialInfo from(KakaoUserInfo kakaoUserInfo) {
        return of(
                AuthProvider.KAKAO,
                kakaoUserInfo.id()
        );
    }

    private SocialInfo(AuthProvider authProvider, String socialId) {
        this.authProvider = authProvider;
        this.socialId = socialId;
    }
}
