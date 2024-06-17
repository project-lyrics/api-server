package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.service.social.SocialService;
import com.projectlyrics.server.domain.auth.service.social.SocialServiceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthQueryService {

  private final SocialServiceFactory socialServiceFactory;

  public AuthSocialInfo getAuthSocialInfo(String socialAccessToken, AuthProvider authProvider) {
    SocialService socialService = socialServiceFactory.getSocialServiceFrom(authProvider);

    return socialService.getSocialData(socialAccessToken);
  }
}
