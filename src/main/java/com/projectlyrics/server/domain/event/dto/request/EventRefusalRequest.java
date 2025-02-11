package com.projectlyrics.server.domain.event.dto.request;

public record EventRefusalRequest(
        Long eventId,
        Long userId
) {
}
