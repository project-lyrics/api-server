package com.projectlyrics.server.domain.event.dto.response;

import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.event.domain.Event;

public record EventGetResponse (
        Long id,
        String popupImageUrl,
        String bannerImageUrl,
        String redirectUrl
) implements CursorResponse {

    public static EventGetResponse of(Event event) {
        return new EventGetResponse(
                event.getId(),
                event.getPopupImageUrl(),
                event.getBannerImageUrl(),
                event.getRedirectUrl()
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
