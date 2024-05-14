package com.projectlyrics.server.domain.auth.service.kakao;

import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.service.SocialService;
import com.projectlyrics.server.domain.auth.service.kakao.dto.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoSocialService implements SocialService {

  private final KakaoSocialDataApiClient kakaoApiClient;

  @Override
  public UserInfoResponse getSocialData(String socialAccessToken) {
    KakaoUserInfoResponse kakaoUserInfo = getUserInfo(socialAccessToken);

    return kakaoUserInfo.toUserInfo();
  }

  private KakaoUserInfoResponse getUserInfo(String accessToken) {
    return kakaoApiClient.getUserInfo(accessToken);
  }
}
