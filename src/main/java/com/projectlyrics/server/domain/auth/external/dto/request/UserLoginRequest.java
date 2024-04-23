package com.projectlyrics.server.domain.auth.external.dto.request;

import com.projectlyrics.server.domain.auth.external.AuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserLoginRequest(
    @NotBlank
    @Schema(description = "인가 코드 값")
    String authorizationCode,
    @NotBlank
    @Schema(description = "리다이렉트 uri 값")
    String redirectUri,
    @NotNull(message = "로그인 종류가 입력되지 않았습니다.")
    @Schema(description = "로그인 타입")
    AuthProvider authProvider
) {

}
