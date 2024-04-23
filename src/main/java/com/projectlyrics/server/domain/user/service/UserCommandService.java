package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.user.dto.response.LoginResponse;
import com.projectlyrics.server.domain.auth.external.dto.request.UserLoginRequest;

public interface UserCommandService {

  LoginResponse login(UserLoginRequest request);
}
