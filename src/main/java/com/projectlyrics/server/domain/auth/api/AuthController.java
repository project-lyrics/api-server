package com.projectlyrics.server.domain.auth.api;

import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.common.dto.SuccessResponse;
import com.projectlyrics.server.domain.user.dto.response.LoginResponse;
import com.projectlyrics.server.domain.user.usecase.command.UserAuthUseCase;
import com.projectlyrics.server.global.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

  private final UserAuthUseCase userAuthUseCase;

  @PostMapping("/login")
  public ResponseEntity<SuccessResponse<LoginResponse>> signIn(
      @RequestHeader("Authorization") String socialAccessToken,
      @RequestBody UserLoginRequest loginRequest
  ) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(SuccessResponse.of(
            SuccessMessage.LOGIN_SUCCESS,
            userAuthUseCase.signIn(socialAccessToken, loginRequest)
        ));
  }
}
