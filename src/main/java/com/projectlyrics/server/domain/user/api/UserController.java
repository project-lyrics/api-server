package com.projectlyrics.server.domain.user.api;

import com.projectlyrics.server.domain.auth.external.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.common.dto.SuccessResponse;
import com.projectlyrics.server.domain.user.dto.response.LoginResponse;
import com.projectlyrics.server.domain.user.usecase.command.UserAuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController implements UserControllerSwagger {

  private final UserAuthUseCase userAuthUseCase;

  @PostMapping("/login/oauth2/kakao")
  public SuccessResponse<LoginResponse> login(
      @RequestBody UserLoginRequest loginRequest
  ) {
    return SuccessResponse.of(
            HttpStatus.CREATED.value(),
            userAuthUseCase.login(loginRequest)
    );
  }
}
