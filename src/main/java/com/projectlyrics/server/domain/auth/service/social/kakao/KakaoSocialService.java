package com.projectlyrics.server.domain.auth.service.social.kakao;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.auth.service.social.SocialService;
import com.projectlyrics.server.domain.auth.service.social.kakao.dto.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoSocialService implements SocialService {

    private static final String TOKEN = "Bearer %s";

    private final KakaoSocialDataApiClient kakaoApiClient;

    @Override
    public AuthProvider getAuthProvider() {
        return AuthProvider.KAKAO;
    }

    @Override
    public AuthSocialInfo getSocialData(String socialAccessToken) {
        KakaoUserInfoResponse kakaoUserInfo = getUserInfo(socialAccessToken);

        return kakaoUserInfo.toKakaoUserInfo();
    }

    private KakaoUserInfoResponse getUserInfo(String accessToken) {
        return kakaoApiClient.getUserInfo(TOKEN.formatted(accessToken));
    }
}
