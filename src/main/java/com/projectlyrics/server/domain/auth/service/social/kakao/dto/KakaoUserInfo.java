package com.projectlyrics.server.domain.auth.service.social.kakao.dto;

public record KakaoUserInfo(
        String id,
        KakaoAccount kakaoAccount
) {
}
