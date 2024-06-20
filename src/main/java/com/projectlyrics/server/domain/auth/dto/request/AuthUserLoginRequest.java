package com.projectlyrics.server.domain.auth.dto.request;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AuthUserLoginRequest(
        @NotNull(message = "로그인 종류가 입력되지 않았습니다.")
        @Schema(description = "로그인 타입")
        AuthProvider authProvider
) {
}
