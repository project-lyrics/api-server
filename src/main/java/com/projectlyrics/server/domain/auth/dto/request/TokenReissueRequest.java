package com.projectlyrics.server.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record TokenReissueRequest(
        @NotNull
        String refreshToken
) {
}
