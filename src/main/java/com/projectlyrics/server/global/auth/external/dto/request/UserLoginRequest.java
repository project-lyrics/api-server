package com.projectlyrics.server.global.auth.external.dto.request;

import com.projectlyrics.server.global.auth.external.AuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserLoginRequest(
        @NotBlank
        @Schema(name = "리다이렉트 uri 값")
        String redirectUri,
        @NotNull(message = "로그인 종류가 입력되지 않았습니다.")
        @Schema(name = "로그인 타입")
        AuthProvider authProvider
) {
}
