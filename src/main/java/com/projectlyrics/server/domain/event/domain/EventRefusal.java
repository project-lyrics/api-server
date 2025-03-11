package com.projectlyrics.server.domain.event.domain;

import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
    private LocalDate deadline;

    private String deviceId;

    private EventRefusal(
            Event event,
            User user,
            LocalDate deadline
    ) {
        this.refusal = true;
        this.event = event;
        this.user = user;
        this.deadline = deadline;
    }

    private EventRefusal(
            Event event,
            String deviceId,
            LocalDate deadline
    ) {
        this.refusal = true;
        this.event = event;
        this.deviceId = deviceId;
        this.deadline = deadline;
    }

    public static EventRefusal create(EventRefusalCreateByUser eventRefusalCreateByUser) {
        return new EventRefusal(
                eventRefusalCreateByUser.event(),
                eventRefusalCreateByUser.user(),
                LocalDate.now().plusDays(eventRefusalCreateByUser.refusalPeriod())
        );
    }

    public static EventRefusal create(EventRefusalCreateByDevice eventRefusalCreateByDevice) {
        return new EventRefusal(
                eventRefusalCreateByDevice.event(),
                eventRefusalCreateByDevice.deviceId(),
                LocalDate.now().plusDays(eventRefusalCreateByDevice.refusalPeriod())
        );
    }
}
