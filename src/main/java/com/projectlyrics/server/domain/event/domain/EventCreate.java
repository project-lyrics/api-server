package com.projectlyrics.server.domain.event.domain;

import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;

import java.time.LocalDateTime;

public record EventCreate(
        String imageUrl,
        String redirectUrl,
        LocalDateTime dueDate
) {
    public static EventCreate of(EventCreateRequest request) {
        return new EventCreate(
                request.imageUrl(),
                request.redirectUrl(),
                request.dueDate()
        );
    }
}
