package com.projectlyrics.server.domain.auth.dto.request;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import jakarta.validation.constraints.NotNull;

public record AuthSignInRequest(

        @NotNull(message = "소셜 엑세스 토큰이 입력되지 않았습니다.")
        String socialAccessToken,

        @NotNull(message = "로그인 종류가 입력되지 않았습니다.")
        AuthProvider authProvider
) {
}
