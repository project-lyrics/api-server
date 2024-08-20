package com.projectlyrics.server.domain.auth.service.social;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.SocialInfo;

public interface SocialService {

    SocialInfo getSocialData(String socialAccessToken);

    AuthProvider getAuthProvider();
}
