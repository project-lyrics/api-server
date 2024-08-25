package com.projectlyrics.server.domain.notification.api.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record PublicNotificationCreateRequest(
        @NotEmpty(message = "알림 내용이 비어 있습니다.")
        String content
) {
}
