package com.projectlyrics.server.domain.event.domain;

import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;

import java.time.LocalDateTime;

public record EventCreate(
        String popupImageUrl,
        String bannerImageUrl,
        String redirectUrl,
        LocalDateTime dueDate
) {
    public static EventCreate of(EventCreateRequest request) {
        return new EventCreate(
                request.popupImageUrl(),
                request.bannerImageUrl(),
                request.redirectUrl(),
                request.dueDate().atStartOfDay()
        );
    }
}
