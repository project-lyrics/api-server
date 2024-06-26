package com.projectlyrics.server.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.user.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Year;
import java.util.List;

public record AuthSignUpRequest(
        @NotBlank
        @Schema(description = "소셜 엑세스 토큰")
        String socialAccessToken,

        @NotNull
        @Schema(description = "소셜 로그인 제공 서비스", implementation = AuthProvider.class)
        AuthProvider authProvider,

        @NotBlank
        @Schema(description = "닉네임")
        String username,

        @Schema(description = "성별", nullable = true)
        Gender gender,

        @DateTimeFormat(pattern = "yyyy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy", timezone = "Asia/Seoul")
        @Schema(description = "출생년도", nullable = true, implementation = String.class)
        Year birthYear,

        @Valid
        List<TermsInput> terms
) {

    public record TermsInput(
            @NotNull
            @Schema(description = "약관 동의")
            boolean agree,

            @NotNull
            @Schema(description = "약관 제목")
            String title,

            @Schema(description = "약관 내용 링크")
            String agreement
    ) {
    }
}
