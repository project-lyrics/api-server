package com.projectlyrics.server.domain.event.domain;

import com.projectlyrics.server.domain.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "events")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String redirectUrl;
    private String buttonText;
    private LocalDateTime dueDate;

    private Event(
            String imageUrl,
            String redirectUrl,
            String buttonText,
            LocalDateTime dueDate
    ) {
        this.imageUrl = imageUrl;
        this.redirectUrl = redirectUrl;
        this.buttonText = buttonText;
        this.dueDate = dueDate;
    }

    public static Event create(EventCreate eventCreate) {
        return new Event(
                eventCreate.imageUrl(),
                eventCreate.redirectUrl(),
                eventCreate.buttonText(),
                eventCreate.dueDate()
        );
    }

    public static Event createWithId(Long id, EventCreate eventCreate) {
        return new Event(
                id,
                eventCreate.imageUrl(),
                eventCreate.redirectUrl(),
                eventCreate.buttonText(),
                eventCreate.dueDate()
        );
    }
}
