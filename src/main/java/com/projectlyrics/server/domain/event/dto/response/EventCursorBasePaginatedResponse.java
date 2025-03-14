package com.projectlyrics.server.domain.event.dto.response;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;

public record EventCursorBasePaginatedResponse (
        int refusalPeriod,
        CursorBasePaginatedResponse<EventGetResponse> event

) {
}
