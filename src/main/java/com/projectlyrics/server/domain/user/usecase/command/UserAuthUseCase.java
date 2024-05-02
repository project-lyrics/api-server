package com.projectlyrics.server.domain.user.usecase.command;

import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.user.dto.response.LoginResponse;

public interface UserAuthUseCase {

  LoginResponse signIn(String socialAccessToken, UserLoginRequest request);
}
