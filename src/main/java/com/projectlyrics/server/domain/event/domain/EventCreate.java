package com.projectlyrics.server.domain.event.domain;

import java.time.LocalDateTime;

public record EventCreate(
        String imageUrl,
        String redirectUrl,
        LocalDateTime dueDate
) { }
