package com.projectlyrics.server.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.user.entity.Gender;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Year;

public record AuthSignUpRequest(
        @NotBlank
        String socialAccessToken,

        @NotBlank
        AuthProvider authProvider,

        @NotBlank
        String username,
        Gender gender,

        @DateTimeFormat(pattern = "yyyy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy", timezone = "Asia/Seoul")
        Year birthYear,

        boolean isAbove14,
        boolean termsOfService,
        boolean privacyPolicy
) {
}
