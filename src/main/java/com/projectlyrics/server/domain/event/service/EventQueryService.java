package com.projectlyrics.server.domain.event.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.event.dto.response.EventGetResponse;
import com.projectlyrics.server.domain.event.repository.EventQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EventQueryService {

    private final EventQueryRepository eventQueryRepository;

    public CursorBasePaginatedResponse<EventGetResponse> getAllExcludingRefusals(Long userId, Long cursor, int size) {
        Slice<EventGetResponse> events = eventQueryRepository.findAllExcludingRefusals(userId, cursor, PageRequest.ofSize(size))
                .map(EventGetResponse::of);

        return CursorBasePaginatedResponse.of(events);

    }
}
