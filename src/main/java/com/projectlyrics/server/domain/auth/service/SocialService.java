package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;

public abstract class SocialService {

  public abstract UserInfoResponse login(UserLoginRequest loginRequest);
}
