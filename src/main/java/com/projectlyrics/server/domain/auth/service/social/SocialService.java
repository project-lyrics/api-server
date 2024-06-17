package com.projectlyrics.server.domain.auth.service.social;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;

public interface SocialService {

  AuthSocialInfo getSocialData(String socialAccessToken);
  AuthProvider getAuthProvider();
}
