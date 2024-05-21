package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.user.dto.response.UserLoginResponse;

public interface UserCommandService {

  UserLoginResponse signIn(String socialAccessToken, UserLoginRequest request);
}
