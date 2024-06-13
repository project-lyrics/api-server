package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.service.social.apple.AppleSocialService;
import com.projectlyrics.server.domain.auth.service.social.kakao.KakaoSocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthQueryService {

  private final KakaoSocialService kakaoSocialService;
  private final AppleSocialService appleSocialService;

  public AuthSocialInfo getAuthSocialInfo(String socialAccessToken, AuthProvider authProvider) {
    return switch (authProvider) {
      case KAKAO -> kakaoSocialService.getSocialData(socialAccessToken);
      case APPLE -> appleSocialService.getSocialData(socialAccessToken);
    };
  }
}
