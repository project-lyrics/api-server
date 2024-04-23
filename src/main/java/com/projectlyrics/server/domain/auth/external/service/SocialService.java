package com.projectlyrics.server.domain.auth.external.service;

import com.projectlyrics.server.domain.auth.external.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.external.dto.response.UserInfoResponse;

public abstract class SocialService {

  public abstract UserInfoResponse login(UserLoginRequest loginRequest);
}
