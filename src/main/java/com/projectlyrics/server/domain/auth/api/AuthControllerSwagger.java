package com.projectlyrics.server.domain.auth.api;

import com.projectlyrics.server.domain.auth.external.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.common.dto.ErrorResponse;
import com.projectlyrics.server.domain.common.dto.SuccessResponse;
import com.projectlyrics.server.domain.user.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

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
              "1. 요청한 값이 유효하지 않습니다.\n" + "2. 인가 코드가 만료되었습니다.\n",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
          @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
      }
  )
  ResponseEntity<SuccessResponse<LoginResponse>> login(
      @RequestBody UserLoginRequest loginRequest
  );
}
