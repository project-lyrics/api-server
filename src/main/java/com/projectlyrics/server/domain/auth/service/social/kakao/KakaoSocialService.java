package com.projectlyrics.server.domain.auth.service.social.kakao;

import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.auth.service.social.SocialService;
import com.projectlyrics.server.domain.auth.service.social.kakao.dto.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoSocialService implements SocialService {

  private final KakaoSocialDataApiClient kakaoApiClient;

  @Override
  public AuthSocialInfo getSocialData(String socialAccessToken) {
    KakaoUserInfoResponse kakaoUserInfo = getUserInfo(socialAccessToken);

    return kakaoUserInfo.toUserInfo();
  }

  private KakaoUserInfoResponse getUserInfo(String accessToken) {
    return kakaoApiClient.getUserInfo(accessToken);
  }
}
