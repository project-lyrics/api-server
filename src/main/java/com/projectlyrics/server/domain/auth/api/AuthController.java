package com.projectlyrics.server.domain.auth.api;

import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.UserTokenReissueResponse;
import com.projectlyrics.server.domain.common.dto.SuccessResponse;
import com.projectlyrics.server.domain.common.message.SuccessMessage;
import com.projectlyrics.server.domain.user.dto.response.UserLoginResponse;
import com.projectlyrics.server.domain.user.service.UserCommandService;
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

  private final UserCommandService userCommandService;

  @PostMapping("/login")
  public ResponseEntity<SuccessResponse<UserLoginResponse>> signIn(
      @RequestHeader("Authorization") String socialAccessToken,
      @RequestBody UserLoginRequest loginRequest
  ) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(SuccessResponse.of(
            SuccessMessage.LOGIN_SUCCESS,
            userCommandService.signIn(socialAccessToken, loginRequest)
        ));
  }

  @PostMapping("/token")
  public ResponseEntity<SuccessResponse<UserTokenReissueResponse>> reissueToken(
      @RequestHeader("Authorization") String refreshToken
  ) {
    return ResponseEntity
        .ok(SuccessResponse.of(
            SuccessMessage.TOKEN_REISSUE_SUCCESS,
            userCommandService.reissueAccessToken(refreshToken)
        ));
  }
}
