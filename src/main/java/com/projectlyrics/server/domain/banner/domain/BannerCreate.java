package com.projectlyrics.server.domain.banner.domain;

import com.projectlyrics.server.domain.banner.dto.request.BannerCreateRequest;
import java.time.LocalDateTime;

public record BannerCreate(
        String imageUrl,
        String redirectUrl,
        LocalDateTime startDate,
        LocalDateTime dueDate
) {
    public static BannerCreate of(BannerCreateRequest request) {
        return new BannerCreate(
                request.imageUrl(),
                request.redirectUrl(),
                request.startDate() == null ? null: request.startDate().atStartOfDay(),
                request.dueDate() == null ? null: request.dueDate().atStartOfDay()
        );
    }
}
