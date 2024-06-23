package com.projectlyrics.server.domain.auth.dto.request;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AuthUserLoginRequest(
        @NotNull(message = "로그인 종류가 입력되지 않았습니다.")
        @Schema(description = "로그인 타입")
        AuthProvider authProvider,
        @NotNull(message = "사용자 역할이 입력되지 않았습니다.")
        @Schema(description = "사용자 역할")
        Role role
) {
}
