package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.discipline.domain.Discipline;
import com.projectlyrics.server.domain.discipline.domain.DisciplineCreate;
import com.projectlyrics.server.domain.discipline.domain.DisciplineReason;
import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import com.projectlyrics.server.domain.user.entity.User;

public class DisciplineFixture extends BaseFixture {

    public static Discipline createForAll(Artist artist, User user) {
        return Discipline.createWithId(
                getUniqueId(),
                DisciplineCreate.of(
                        user,
                        artist,
                        DisciplineReason.COMMERCIAL_ADS,
                        DisciplineType.ALL_3DAYS
                )
        );
    }

    public static Discipline createForArtist(Artist artist, User user) {
        return Discipline.createWithId(
                getUniqueId(),
                DisciplineCreate.of(
                        user,
                        artist,
                        DisciplineReason.COMMERCIAL_ADS,
                        DisciplineType.ARTIST_3DAYS
                )
        );
    }
}
