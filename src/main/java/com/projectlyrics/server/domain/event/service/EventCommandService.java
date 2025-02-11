package com.projectlyrics.server.domain.event.service;

import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.domain.EventCreate;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.repository.EventCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventCommandService {

    private final EventCommandRepository eventCommandRepository;

    public Event create(EventCreateRequest request) {
        return eventCommandRepository.save(Event.create(EventCreate.of(request)));
    }
}
