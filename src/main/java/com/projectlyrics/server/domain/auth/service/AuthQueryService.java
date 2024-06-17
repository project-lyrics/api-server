package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.service.social.SocialService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthQueryService {

  private final Map<String, SocialService> socialServices;

  public AuthSocialInfo getAuthSocialInfo(String socialAccessToken, AuthProvider authProvider) {
    SocialService socialService = socialServices.get(authProvider.name());

    if (socialService == null)
      throw new IllegalArgumentException("unsupported auth provider");

    return socialService.getSocialData(socialAccessToken);
  }
}
