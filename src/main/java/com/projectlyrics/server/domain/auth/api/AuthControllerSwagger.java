package com.projectlyrics.server.domain.auth.api;

import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenReissueResponse;
import com.projectlyrics.server.domain.common.dto.ErrorResponse;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "사용자 보안 관련 API")
public interface AuthControllerSwagger {

    @Operation(
            summary = "사용자 로그인 API",
            description = "소셜 액세스 토큰과 소셜 플랫폼 종류를 받아 기존의 사용자에게 토큰을 발급합니다."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부에 오류가 발생했습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<AuthTokenResponse> signIn(
            @RequestBody AuthSignInRequest loginRequest
    );

    @Operation(
            summary = "사용자 가입 API",
            description = "사용자 정보를 받아 사용자를 가입시킵니다."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "사용자 가입이 완료되었습니다."),
                    @ApiResponse(responseCode = "400", description = "요청한 값이 유효하지 않습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<AuthTokenResponse> signUp(
            @RequestBody AuthSignUpRequest request
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
