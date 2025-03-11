package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.domain.EventCreate;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventFixture extends BaseFixture{

    private Long id;
    private String imageUrl = "imageUrl";
    private String redirectUrl = "redirectUrl";
    private LocalDateTime dueDate = LocalDate.now().atTime(23, 59, 59);

    public static Event create() {
        return Event.createWithId(
                getUniqueId(),
                new EventCreate(
                      "imageUrl",
                        "redirectUrl",
                        "button",
                        LocalDate.now().atTime(23, 59, 59)
                )
        );
    }
}
