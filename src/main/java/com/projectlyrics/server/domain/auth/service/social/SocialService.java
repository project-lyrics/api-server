package com.projectlyrics.server.domain.auth.service.social;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;

public interface SocialService {

    AuthSocialInfo getSocialData(String socialAccessToken);

    AuthProvider getAuthProvider();
}
