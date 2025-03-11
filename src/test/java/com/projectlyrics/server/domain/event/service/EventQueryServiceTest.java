package com.projectlyrics.server.domain.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.domain.EventCreate;
import com.projectlyrics.server.domain.event.domain.EventRefusal;
import com.projectlyrics.server.domain.event.domain.EventRefusalCreateByDevice;
import com.projectlyrics.server.domain.event.domain.EventRefusalCreateByUser;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.dto.response.EventGetResponse;
import com.projectlyrics.server.domain.event.repository.EventCommandRepository;
import com.projectlyrics.server.domain.event.repository.EventQueryRepository;
import com.projectlyrics.server.domain.event.repository.EventRefusalCommandRepository;
import com.projectlyrics.server.domain.event.repository.EventRefusalQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class EventQueryServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    EventQueryRepository eventQueryRepository;

    @Autowired
    EventCommandRepository eventCommandRepository;

    @Autowired
    EventRefusalCommandRepository eventRefusalCommandRepository;

    @Autowired
    EventRefusalQueryRepository eventRefusalQueryRepository;

    @Autowired
    EventQueryService sut;

    private User user;
    private EventCreateRequest activeEventCreateRequest;
    private EventCreateRequest expiredEventCreateRequest;
    String deviceId = "device_id";

    @BeforeEach
    void setUp() {
        user = userCommandRepository.save(UserFixture.create());
        activeEventCreateRequest = new EventCreateRequest(
                "imageUrl",
                "redirectUrl",
                "button",
                LocalDate.now().plusDays(1)
        );
        expiredEventCreateRequest = new EventCreateRequest(
                "imageUrl",
                "redirectUrl",
                "button",
                LocalDate.now().minusDays(1)
        );
    }

    @Test
    void 사용자id를_통해_진행_중인_모든_이벤트를_최신순으로_조회해야_한다() {
        // given
        Event activeEvent1 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        Event activeEvent2 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        eventCommandRepository.save(Event.create(EventCreate.of(expiredEventCreateRequest)));
        Event activeEvent3 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        Event activeEvent4 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        eventCommandRepository.save(Event.create(EventCreate.of(expiredEventCreateRequest)));

        // when
        CursorBasePaginatedResponse<EventGetResponse> result = sut.getAllExceptRefusedByUser(user.getId(), null, 6);

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
    void 디바이스id를_통해_진행_중인_모든_이벤트를_최신순으로_조회해야_한다() {
        // given
        Event activeEvent1 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        Event activeEvent2 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        eventCommandRepository.save(Event.create(EventCreate.of(expiredEventCreateRequest)));
        Event activeEvent3 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        Event activeEvent4 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        eventCommandRepository.save(Event.create(EventCreate.of(expiredEventCreateRequest)));

        // when
        CursorBasePaginatedResponse<EventGetResponse> result = sut.getAllExceptRefusedByDeviceId(deviceId, null, 6);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(4),
                () -> assertThat(result.data().get(0).id()).isEqualTo(activeEvent4.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(activeEvent3.getId()),
                () -> assertThat(result.data().get(2).id()).isEqualTo(activeEvent2.getId()),
                () -> assertThat(result.data().get(3).id()).isEqualTo(activeEvent1.getId())
        );
    }

    @Transactional
    @Test
    void 사용자id로_진행_중인_이벤트_조회시_해당_다바이스id의_거부_만료_기간이_남아있다면_해당_이벤트가_제외되어야_한다() throws Exception{
        // given
        Event activeEvent1 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        Event activeEvent2 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        eventCommandRepository.save(Event.create(EventCreate.of(expiredEventCreateRequest)));
        Event activeEvent3 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        Event activeEvent4 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        eventCommandRepository.save(Event.create(EventCreate.of(expiredEventCreateRequest)));
        eventRefusalCommandRepository.save(EventRefusal.create(new EventRefusalCreateByUser(activeEvent1, user, 0)));
        eventRefusalCommandRepository.save(EventRefusal.create(new EventRefusalCreateByUser(activeEvent3, user, 1)));

        // when
        CursorBasePaginatedResponse<EventGetResponse> result = sut.getAllExceptRefusedByUser(user.getId(), null, 6);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(3),
                () -> assertThat(result.data().get(0).id()).isEqualTo(activeEvent4.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(activeEvent2.getId()),
                () -> assertThat(result.data().get(2).id()).isEqualTo(activeEvent1.getId())
        );
    }

    @Transactional
    @Test
    void 디바이스id로_진행_중인_이벤트_조회시_해당_다바이스id의_거부_만료_기간이_남아있다면_해당_이벤트가_제외되어야_한다() {
        // given
        Event activeEvent1 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        Event activeEvent2 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        eventCommandRepository.save(Event.create(EventCreate.of(expiredEventCreateRequest)));
        Event activeEvent3 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        Event activeEvent4 = eventCommandRepository.save(Event.create(EventCreate.of(activeEventCreateRequest)));
        eventCommandRepository.save(Event.create(EventCreate.of(expiredEventCreateRequest)));
        eventRefusalCommandRepository.save(EventRefusal.create(new EventRefusalCreateByDevice(activeEvent1, deviceId, 0)));
        eventRefusalCommandRepository.save(EventRefusal.create(new EventRefusalCreateByDevice(activeEvent3, deviceId, 1)));

        // when
        CursorBasePaginatedResponse<EventGetResponse> result = sut.getAllExceptRefusedByDeviceId(deviceId, null, 6);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(3),
                () -> assertThat(result.data().get(0).id()).isEqualTo(activeEvent4.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(activeEvent2.getId()),
                () -> assertThat(result.data().get(2).id()).isEqualTo(activeEvent1.getId())
        );
    }
}
