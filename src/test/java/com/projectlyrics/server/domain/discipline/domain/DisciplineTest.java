package com.projectlyrics.server.domain.discipline.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class DisciplineTest {

    @Test
    void DisciplineCreate_객체로부터_Discipline_객체를_생성할_수_있다() {
        // given
        User user = UserFixture.create();
        Artist artist = ArtistFixture.create();
        DisciplineReason disciplineReason = DisciplineReason.COMMERCIAL_ADS;
        DisciplineType disciplineType = DisciplineType.ALL_3MONTHS;

        DisciplineCreate disciplineCreate = new DisciplineCreate(
                user,
                artist,
                disciplineReason,
                disciplineType,
                LocalDateTime.now()
        );

        // when
        Discipline discipline = Discipline.create(disciplineCreate);

        // then
        assertAll(
                () -> assertThat(discipline.getUser().getId()).isEqualTo(user.getId()),
                () -> assertThat(discipline.getArtist().getId()).isEqualTo(artist.getId()),
                () -> assertThat(discipline.getReason()).isEqualTo(disciplineReason),
                () -> assertThat(discipline.getType()).isEqualTo(disciplineType),
                () -> assertThat(discipline.getStartTime().getMinute()).isEqualTo(0),
                () -> assertThat(discipline.getStartTime().getSecond()).isEqualTo(0),
                () -> assertThat(discipline.getStartTime().getNano()).isEqualTo(0),
                () -> assertThat(discipline.getStartTime().plus(disciplineType.getPeriod())).isEqualTo(discipline.getEndTime())
        );
    }

}