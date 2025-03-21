package com.projectlyrics.server.domain.event.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record EventCreateRequest(
        String imageUrl,
        String redirectUrl,
        String buttonText,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate dueDate
) {
}
