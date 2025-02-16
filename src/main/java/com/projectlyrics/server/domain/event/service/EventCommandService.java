package com.projectlyrics.server.domain.event.service;

import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.domain.EventCreate;
import com.projectlyrics.server.domain.event.domain.EventRefusal;
import com.projectlyrics.server.domain.event.domain.EventRefusalCreate;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.repository.EventCommandRepository;
import com.projectlyrics.server.domain.event.repository.EventQueryRepository;
import com.projectlyrics.server.domain.event.repository.EventRefusalCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventCommandService {

    private final EventCommandRepository eventCommandRepository;
    private final EventQueryRepository eventQueryRepository;
    private final EventRefusalCommandRepository eventRefusalCommandRepository;
    private final UserQueryRepository userQueryRepository;

    public Event create(EventCreateRequest request) {
        return eventCommandRepository.save(Event.create(EventCreate.of(request)));
    }

    public EventRefusal refuse(Long eventId, Long userId) {
        Event event = eventQueryRepository.findById(eventId);
        User user = userQueryRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        EventRefusal eventReceipt = EventRefusal.create(new EventRefusalCreate(event, user));
        return eventRefusalCommandRepository.save(eventReceipt);
    }
}
