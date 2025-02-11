package com.projectlyrics.server.domain.event.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "comments")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String redirectUrl;
    private LocalDateTime dueDate;

    private Event(
            String imageUrl,
            String redirectUrl,
            LocalDateTime dueDate
    ) {
        this.imageUrl = imageUrl;
        this.redirectUrl = redirectUrl;
        this.dueDate = dueDate;
    }

    public static Event Event(EventCreate eventCreate) {
        return new Event(
                eventCreate.imageUrl(),
                eventCreate.redirectUrl(),
                eventCreate.dueDate()
        );
    }
}
