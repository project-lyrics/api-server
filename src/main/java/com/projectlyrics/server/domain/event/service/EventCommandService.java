package com.projectlyrics.server.domain.event.service;

import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.domain.EventCreate;
import com.projectlyrics.server.domain.event.domain.EventRefusal;
import com.projectlyrics.server.domain.event.domain.EventRefusalCreateByDevice;
import com.projectlyrics.server.domain.event.domain.EventRefusalCreateByUser;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.exception.EventRefusalNotFoundException;
import com.projectlyrics.server.domain.event.repository.EventCommandRepository;
import com.projectlyrics.server.domain.event.repository.EventQueryRepository;
import com.projectlyrics.server.domain.event.repository.EventRefusalCommandRepository;
import com.projectlyrics.server.domain.event.repository.EventRefusalQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import java.util.function.Supplier;
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

        return upsertEventRefusal(
                () -> eventRefusalQueryRepository.findByEventIdAndUserId(event.getId(), user.getId()),
                () -> EventRefusal.create(new EventRefusalCreateByUser(event, user))
        );
    }

    public synchronized EventRefusal refuseByDeviceId(Long eventId, String deviceId) {
        Event event = eventQueryRepository.findById(eventId);

        return upsertEventRefusal(
                () -> eventRefusalQueryRepository.findByEventIdAndDeviceId(event.getId(), deviceId),
                () -> EventRefusal.create(new EventRefusalCreateByDevice(event, deviceId))
        );
    }

    private EventRefusal upsertEventRefusal(
            Supplier<EventRefusal> findExistingRefusal,
            Supplier<EventRefusal> createNewRefusal
    ) {
        try {
            EventRefusal eventRefusal = findExistingRefusal.get();
            eventRefusal.touch();
            return eventRefusal;
        } catch (EventRefusalNotFoundException e) {
            return eventRefusalCommandRepository.save(createNewRefusal.get());
        }
    }
}
