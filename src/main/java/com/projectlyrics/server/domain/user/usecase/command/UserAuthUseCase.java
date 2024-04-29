package com.projectlyrics.server.domain.user.usecase.command;

import com.projectlyrics.server.domain.user.dto.response.LoginResponse;
import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;

public interface UserAuthUseCase {

  LoginResponse login(UserLoginRequest request);
}
