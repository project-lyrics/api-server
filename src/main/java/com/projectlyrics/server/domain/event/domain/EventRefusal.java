package com.projectlyrics.server.domain.event.domain;

import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "event_refusal")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventRefusal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean refusal;

    @JoinColumn(name = "event_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String deviceId;

    private EventRefusal(
            Event event,
            User user
    ) {
        this.refusal = true;
        this.event = event;
        this.user = user;
    }

    private EventRefusal(
            Event event,
            String deviceId
    ) {
        this.refusal = true;
        this.event = event;
        this.deviceId = deviceId;
    }

    public static EventRefusal create(EventRefusalCreateByUser eventRefusalCreateByUser) {
        return new EventRefusal(
                eventRefusalCreateByUser.event(),
                eventRefusalCreateByUser.user()
        );
    }

    public static EventRefusal create(EventRefusalCreateByDevice eventRefusalCreateByDevice) {
        return new EventRefusal(
                eventRefusalCreateByDevice.event(),
                eventRefusalCreateByDevice.deviceId()
        );
    }
}
