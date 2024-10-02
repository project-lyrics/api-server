package com.projectlyrics.server.domain.notification.api.dto.response;

import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.notification.domain.NotificationType;

public record NotificationGetResponse(
        Long id,
        NotificationType type,
        String content,
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
