package com.projectlyrics.server.domain.user.api;

import com.projectlyrics.server.domain.user.dto.response.LoginResponse;
import com.projectlyrics.server.domain.user.service.UserCommandService;
import com.projectlyrics.server.global.auth.external.dto.request.UserLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

  private final UserCommandService userCommandService;

  @Operation(
      summary = "사용자 로그인 API",
      description = "인가 코드와 로그인 요청 데이터를 받아 사용자를 가입시킵니다."
  )
  @PostMapping("/login/oauth2/kakao")
  public ResponseEntity<LoginResponse> login(
      @RequestBody UserLoginRequest loginRequest
  ) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(userCommandService.login(loginRequest));
  }
}
