package com.projectlyrics.server.domain.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.domain.EventCreate;
import com.projectlyrics.server.domain.event.domain.EventReceipt;
import com.projectlyrics.server.domain.event.domain.EventReceiptCreate;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.dto.response.EventGetResponse;
import com.projectlyrics.server.domain.event.repository.EventCommandRepository;
import com.projectlyrics.server.domain.event.repository.EventQueryRepository;
import com.projectlyrics.server.domain.event.repository.EventReceiptCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EventQueryServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    EventQueryRepository eventQueryRepository;

    @Autowired
    EventCommandRepository eventCommandRepository;

    @Autowired
    EventReceiptCommandRepository eventReceiptCommandRepository;

    @Autowired
    EventQueryService sut;

    private User user;
    private EventCreateRequest activeEventCreateRequest;
    private EventCreateRequest expiredEventCreateRequest;

    @BeforeEach
    void setUp() {
        user = userCommandRepository.save(UserFixture.create());
        activeEventCreateRequest = new EventCreateRequest(
                "imageUrl",
                "redirectUrl",
                LocalDate.now().plusDays(1)
        );
        expiredEventCreateRequest = new EventCreateRequest(
                "imageUrl",
                "redirectUrl",
                LocalDate.now().minusDays(1)
        );
    }

    @Test
    void 진행_중인_모든_이벤트를_최신순으로_조회해야_한다() {
        // given
        Event activeEvent1 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        Event activeEvent2 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        eventCommandRepository.save(Event.create(EventCreate.of(expiredEventCreateRequest)));
        Event activeEvent3 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        Event activeEvent4 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        eventCommandRepository.save(Event.create(EventCreate.of(expiredEventCreateRequest)));

        // when
        CursorBasePaginatedResponse<EventGetResponse> result = sut.getAllExcludingRefusals(user.getId(), null, 6);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(4),
                () -> assertThat(result.data().get(0).id()).isEqualTo(activeEvent4.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(activeEvent3.getId()),
                () -> assertThat(result.data().get(2).id()).isEqualTo(activeEvent2.getId()),
                () -> assertThat(result.data().get(3).id()).isEqualTo(activeEvent1.getId())
        );
    }

    @Test
    void 진행_중인_모든_이벤트_조회시_사용자가_이벤트를_거부한_내역이_있으면_제외해야_한다() {
        // given
        Event activeEvent1 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        Event activeEvent2 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        eventCommandRepository.save(Event.create(EventCreate.of(expiredEventCreateRequest)));
        Event activeEvent3 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        Event activeEvent4 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        eventCommandRepository.save(Event.create(EventCreate.of(expiredEventCreateRequest)));
        eventReceiptCommandRepository.save(EventReceipt.create(new EventReceiptCreate(activeEvent1, user)));
        eventReceiptCommandRepository.save(EventReceipt.create(new EventReceiptCreate(activeEvent3, user)));

        // when
        CursorBasePaginatedResponse<EventGetResponse> result = sut.getAllExcludingRefusals(user.getId(), null, 6);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(2),
                () -> assertThat(result.data().get(0).id()).isEqualTo(activeEvent4.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(activeEvent2.getId())
        );
    }
}
