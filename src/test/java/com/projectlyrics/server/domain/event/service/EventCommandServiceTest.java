package com.projectlyrics.server.domain.event.service;

import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.domain.EventReceipt;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.repository.EventQueryRepository;
import com.projectlyrics.server.domain.event.repository.EventReceiptQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EventCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    EventQueryRepository eventQueryRepository;

    @Autowired
    EventReceiptQueryRepository eventReceiptQueryRepository;

    @Autowired
    EventCommandService sut;

    @Test
    void 이벤트를_발행해야_한다() {
        // given
        EventCreateRequest request = new EventCreateRequest("imageUrl", "redirectUrl", LocalDate.now());

        // when
        Event event = sut.create(request);

        // then
        Event result = eventQueryRepository.findById(event.getId());
        assertAll(
                () -> assertThat(result.getImageUrl()).isEqualTo(request.imageUrl()),
                () -> assertThat(result.getRedirectUrl()).isEqualTo(request.redirectUrl()),
                () -> assertThat(result.getDueDate()).isEqualTo(request.dueDate().atStartOfDay())
        );
    }

    @Test
    void 이벤트를_거부할_수_있다() {
        // given
        User user = userCommandRepository.save(UserFixture.create());

        EventCreateRequest request = new EventCreateRequest("imageUrl", "redirectUrl", LocalDate.now());
        Event event = sut.create(request);

        // when
        sut.refuse(event.getId(), user.getId());

        // then
        EventReceipt result = eventReceiptQueryRepository.findByEventIdAndUserId(event.getId(), user.getId());
        assertAll(
                () -> assertThat(result.getEvent().getId()).isEqualTo(event.getId()),
                () -> assertThat(result.getUser().getId()).isEqualTo(user.getId())
        );
    }
}