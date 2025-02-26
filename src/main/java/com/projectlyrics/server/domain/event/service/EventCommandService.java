package com.projectlyrics.server.domain.event.service;

import com.projectlyrics.server.domain.event.domain.*;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.exception.EventRefusalNotFoundException;
import com.projectlyrics.server.domain.event.repository.EventCommandRepository;
import com.projectlyrics.server.domain.event.repository.EventQueryRepository;
import com.projectlyrics.server.domain.event.repository.EventRefusalCommandRepository;
import com.projectlyrics.server.domain.event.repository.EventRefusalQueryRepository;
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
    private final EventRefusalQueryRepository eventRefusalQueryRepository;
    private final UserQueryRepository userQueryRepository;

    public Event create(EventCreateRequest request) {
        return eventCommandRepository.save(Event.create(EventCreate.of(request)));
    }

    public synchronized EventRefusal refuseByUser(Long eventId, Long userId) {
        Event event = eventQueryRepository.findById(eventId);
        User user = userQueryRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return upsertEventRefusalByUser(event, user);
    }

    private EventRefusal upsertEventRefusalByUser(Event event, User user) {
        try {
            EventRefusal eventRefusal = eventRefusalQueryRepository.findByEventIdAndUserId(event.getId(), user.getId());
            eventRefusal.touch();

            return eventRefusal;
        } catch (EventRefusalNotFoundException e) {
            return eventRefusalCommandRepository.save(EventRefusal.create(new EventRefusalCreateByUser(event, user)));
        }
    }

    public synchronized EventRefusal refuseByDeviceId(Long eventId, String deviceId) {
        Event event = eventQueryRepository.findById(eventId);

        return upsertEventRefusalByDeviceId(event, deviceId);
    }

    private EventRefusal upsertEventRefusalByDeviceId(Event event, String deviceId) {
        try {
            EventRefusal eventRefusal = eventRefusalQueryRepository.findByEventIdAndDeviceId(event.getId(), deviceId);
            eventRefusal.touch();

            return eventRefusal;
        } catch (EventRefusalNotFoundException e) {
            return eventRefusalCommandRepository.save(EventRefusal.create(new EventRefusalCreateByDevice(event, deviceId)));
        }
    }
}
