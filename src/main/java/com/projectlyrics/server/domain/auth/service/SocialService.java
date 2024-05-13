package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;

public interface SocialService {

  UserInfoResponse getSocialData(String socialAccessToken);
}
