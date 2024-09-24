package com.projectlyrics.server.domain.discipline.domain;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.user.entity.User;

public record DisciplineCreate (
        User user,
        Artist artist,
        DisciplineReason reason,
        DisciplineType type

){
    public static DisciplineCreate of(User user, Artist artist, DisciplineReason reason, DisciplineType type) {
        return new DisciplineCreate(user, artist, reason, type);
    }
}
