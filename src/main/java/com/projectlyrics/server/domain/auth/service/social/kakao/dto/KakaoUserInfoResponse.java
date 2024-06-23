package com.projectlyrics.server.domain.auth.service.social.kakao.dto;

import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;

public record KakaoUserInfoResponse(
        String id,
        KakaoAccount kakaoAccount
) {
    public AuthSocialInfo toKakaoUserInfo() {
        return new AuthSocialInfo(
                AuthProvider.KAKAO,
                id,
                kakaoAccount.email()
        );
    }
}
