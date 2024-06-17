package com.projectlyrics.server.domain.auth.api;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.projectlyrics.server.domain.auth.dto.request.AuthUserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenReissueResponse;
import com.projectlyrics.server.domain.common.dto.ErrorResponse;
import com.projectlyrics.server.domain.auth.dto.response.AuthLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "사용자 관련 API")
public interface AuthControllerSwagger {

  @Operation(
      summary = "사용자 로그인 API",
      description = "인가 코드와 로그인 요청 데이터를 받아 사용자를 가입시킵니다."
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "소셜 로그인이 완료되었습니다."),
          @ApiResponse(responseCode = "400", description =
              """
                  1. 요청한 값이 유효하지 않습니다.
                  2. 소셜 액세스 토큰이 만료되었습니다.
                  """,
              content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      }
  )
  ResponseEntity<AuthLoginResponse> signIn(
      @RequestHeader(AUTHORIZATION) String socialAccessToken,
      @RequestBody AuthUserLoginRequest loginRequest
  );

  @Operation(
      summary = "액세스 토큰 재발급 API",
      description = "리프레시 토큰을 받아 새로운 액세스 토큰을 발급합니다."
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "액세스 토큰이 재발급되었습니다."),
          @ApiResponse(responseCode = "400", description = "리프레시 토큰이 유효하지 않습니다.",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      }
  )
  ResponseEntity<AuthTokenReissueResponse> reissueToken(
      @RequestHeader("Authorization") String refreshToken
  );
}
