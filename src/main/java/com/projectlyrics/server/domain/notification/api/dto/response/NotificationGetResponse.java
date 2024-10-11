package com.projectlyrics.server.domain.notification.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.notification.domain.NotificationType;

import java.time.LocalDateTime;

public record NotificationGetResponse(
        Long id,
        NotificationType type,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        boolean checked,
        Long noteId,
        String noteContent,
        String artistImageUrl
) implements CursorResponse {

    @Override
    public long getId() {
        return id;
    }
}
