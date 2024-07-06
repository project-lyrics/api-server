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
import org.springframework.http.MediaType;
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
                    @ApiResponse(responseCode = "200", description = "성공",
                            content = @Content(schema = @Schema(implementation = AuthTokenResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "00001", description = "일부 필드가 잘못된 값으로 입력되었습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "00002", description = "서버 내부에 오류가 발생했습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "00008", description = "{요청 필드 중 하나}가 입력되지 않았습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "01004", description = "토큰이 유효하지 않습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "02000", description = "해당 사용자를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    ResponseEntity<AuthTokenResponse> signIn(
            @RequestBody AuthSignInRequest loginRequest
    );

    @Operation(
            summary = "사용자 가입 API",
            description = "소셜 액세스 토큰과 소셜 플랫폼 종류, 사용자가 입력한 가입 정보를 받아 사용자를 가입시킵니다."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "성공",
                            content = @Content(schema = @Schema(implementation = AuthTokenResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "00001", description = "일부 필드가 잘못된 값으로 입력되었습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "00002", description = "서버 내부에 오류가 발생했습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "00008", description = "{요청 필드 중 하나}가 입력되지 않았습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
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
                    @ApiResponse(responseCode = "200", description = "성공",
                            content = @Content(schema = @Schema(implementation = AuthTokenReissueResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "01002", description = "잘못된 토큰 형식으로 입력되었습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
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
