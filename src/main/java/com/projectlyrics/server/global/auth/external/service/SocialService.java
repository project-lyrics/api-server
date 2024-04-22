package com.projectlyrics.server.global.auth.external.service;

import com.projectlyrics.server.global.auth.external.dto.request.UserLoginRequest;
import com.projectlyrics.server.global.auth.external.dto.response.UserInfoResponse;

public abstract class SocialService {

  public abstract UserInfoResponse login(UserLoginRequest loginRequest);
}
