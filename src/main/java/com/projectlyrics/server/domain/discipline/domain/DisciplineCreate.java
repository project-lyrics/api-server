package com.projectlyrics.server.domain.discipline.domain;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.user.entity.User;
import java.time.LocalDateTime;

public record DisciplineCreate (
        User user,
        Artist artist,
        DisciplineReason reason,
        DisciplineType type,
        LocalDateTime startTime,
        String notificationContent

){
    public static DisciplineCreate of(User user, Artist artist, DisciplineReason reason, DisciplineType type, LocalDateTime startTime, String notificationContent) {
        return new DisciplineCreate(user, artist, reason, type, startTime, notificationContent);
    }
}
