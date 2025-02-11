package com.projectlyrics.server.domain.event.domain;

import com.projectlyrics.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "comments")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventReceipt {

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

    private EventReceipt(
            Event event,
            User user
    ) {
        this.refusal = true;
        this.event = event;
        this.user = user;
    }

    public static EventReceipt create(EventReceiptCreate eventReceiptCreate) {
        return new EventReceipt(
                eventReceiptCreate.event(),
                eventReceiptCreate.user()
        );
    }
}
