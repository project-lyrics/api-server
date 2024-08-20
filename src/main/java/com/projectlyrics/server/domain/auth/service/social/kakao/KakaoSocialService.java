package com.projectlyrics.server.domain.auth.service.social.kakao;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.auth.service.social.SocialService;
import com.projectlyrics.server.domain.auth.service.social.kakao.dto.KakaoUserInfo;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
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
    public SocialInfo getSocialData(String socialAccessToken) {
        KakaoUserInfo kakaoUserInfo = getUserInfo(socialAccessToken);

        return SocialInfo.from(kakaoUserInfo);
    }

    private KakaoUserInfo getUserInfo(String accessToken) {
        return kakaoApiClient.getUserInfo(TOKEN.formatted(accessToken));
    }
}
