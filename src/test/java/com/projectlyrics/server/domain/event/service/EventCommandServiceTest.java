package com.projectlyrics.server.domain.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.domain.EventRefusal;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.repository.EventQueryRepository;
import com.projectlyrics.server.domain.event.repository.EventRefusalQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EventCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    EventQueryRepository eventQueryRepository;

    @Autowired
    EventRefusalQueryRepository eventRefusalQueryRepository;

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
        Optional<EventRefusal> result = eventRefusalQueryRepository.findByEventIdAndUserId(event.getId(), user.getId());
        assertAll(
                () -> assertThat(result).isPresent(),
                () -> assertThat(result.get().getEvent().getId()).isEqualTo(event.getId()),
                () -> assertThat(result.get().getUser().getId()).isEqualTo(user.getId())
        );
    }

    @Test
    void 동일_유저와_이벤트의_거부_기록이_있으면_updatedAt만_갱신한다() {
        // given
        User user = userCommandRepository.save(UserFixture.create());

        EventCreateRequest request = new EventCreateRequest("imageUrl", "redirectUrl", LocalDate.now());
        Event event = sut.create(request);

        // when
        EventRefusal eventRefusalBefore = sut.refuse(event.getId(), user.getId());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 2초 대기
        }
        EventRefusal eventRefusalAfter = sut.refuse(event.getId(), user.getId());

        // then
        assertAll(
                () -> assertThat(eventRefusalBefore.getId()).isEqualTo(eventRefusalAfter.getId()),
                () -> assertThat(eventRefusalBefore.getEvent().getId()).isEqualTo(eventRefusalAfter.getEvent().getId()),
                () -> assertThat(eventRefusalBefore.getUser().getId()).isEqualTo(eventRefusalAfter.getUser().getId()),
                () -> assertThat(eventRefusalBefore.getUpdatedAt().plusSeconds(1).isBefore(eventRefusalAfter.getUpdatedAt()))
        );
    }
}