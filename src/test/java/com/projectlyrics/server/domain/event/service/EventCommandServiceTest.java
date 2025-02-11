package com.projectlyrics.server.domain.event.service;

import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.repository.EventCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EventCommandServiceTest extends IntegrationTest {

    @Autowired
    EventCommandRepository eventCommandRepository;

    @Autowired
    EventCommandService sut;

    @Test
    void 이벤트를_발행해야_한다() {
        // given
        EventCreateRequest request = new EventCreateRequest("imageUrl", "redirectUrl", LocalDateTime.now());

        // when
        Event result = sut.create(request);

        // then
        assertAll(
                () -> assertThat(result.getImageUrl()).isEqualTo(request.imageUrl()),
                () -> assertThat(result.getRedirectUrl()).isEqualTo(request.redirectUrl()),
                () -> assertThat(result.getDueDate()).isEqualTo(request.dueDate())
        );
    }
}