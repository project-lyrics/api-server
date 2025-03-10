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

    private String popupImageUrl;
    private String bannerImageUrl;
    private String redirectUrl;
    private LocalDateTime dueDate;

    private Event(
            String popupImageUrl,
            String bannerImageUrl,
            String redirectUrl,
            LocalDateTime dueDate
    ) {
        this.popupImageUrl = popupImageUrl;
        this.bannerImageUrl = bannerImageUrl;
        this.redirectUrl = redirectUrl;
        this.dueDate = dueDate;
    }

    public static Event create(EventCreate eventCreate) {
        return new Event(
                eventCreate.popupImageUrl(),
                eventCreate.bannerImageUrl(),
                eventCreate.redirectUrl(),
                eventCreate.dueDate()
        );
    }

    public static Event createWithId(Long id, EventCreate eventCreate) {
        return new Event(
                id,
                eventCreate.popupImageUrl(),
                eventCreate.bannerImageUrl(),
                eventCreate.redirectUrl(),
                eventCreate.dueDate()
        );
    }
}
