package com.projectlyrics.server.domain.auth.api;

import static com.projectlyrics.server.domain.auth.api.util.AuthHttpHeaders.ADMIN_SECRET;
import static com.projectlyrics.server.domain.auth.api.util.AuthHttpHeaders.AUTHORIZATION;

import com.projectlyrics.server.domain.auth.dto.request.AuthUserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenReissueResponse;
import com.projectlyrics.server.domain.auth.service.AuthCommandService;
import com.projectlyrics.server.domain.common.dto.SuccessResponse;
import com.projectlyrics.server.domain.common.message.SuccessMessage;
import com.projectlyrics.server.domain.auth.dto.response.AuthLoginResponse;
import jakarta.validation.Valid;
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

  private final AuthCommandService authCommandService;

  @PostMapping("/login")
  public ResponseEntity<SuccessResponse<AuthLoginResponse>> signIn(
      @RequestHeader(AUTHORIZATION) String socialAccessToken,
      @RequestBody @Valid AuthUserLoginRequest loginRequest
  ) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(SuccessResponse.of(
            SuccessMessage.LOGIN_SUCCESS,
            authCommandService.signIn(socialAccessToken, loginRequest)
        ));
  }

  @PostMapping("/token")
  public ResponseEntity<SuccessResponse<AuthTokenReissueResponse>> reissueToken(
      @RequestHeader(AUTHORIZATION) String refreshToken
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of(
            SuccessMessage.TOKEN_REISSUE_SUCCESS,
            authCommandService.reissueAccessToken(refreshToken)
        ));
  }

  @PostMapping("/admin")
  public ResponseEntity<SuccessResponse<AuthLoginResponse>> signIn(
      @RequestHeader(AUTHORIZATION) String socialAccessToken,
      @RequestHeader(ADMIN_SECRET) String adminSecret,
      @RequestBody @Valid AuthUserLoginRequest loginRequest
  ) {
    authCommandService.validateAdminSecret(adminSecret);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(SuccessResponse.of(
            SuccessMessage.ADMIN_CREATE_SUCCESS,
            authCommandService.signIn(socialAccessToken, loginRequest)
        ));
  }
}
