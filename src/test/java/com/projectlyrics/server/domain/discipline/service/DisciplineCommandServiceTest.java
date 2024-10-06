package com.projectlyrics.server.domain.discipline.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.discipline.domain.Discipline;
import com.projectlyrics.server.domain.discipline.domain.DisciplineReason;
import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import com.projectlyrics.server.domain.discipline.dto.request.DisciplineCreateRequest;
import com.projectlyrics.server.domain.discipline.repository.DisciplineQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DisciplineCommandServiceTest extends IntegrationTest {
    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    DisciplineQueryRepository disciplineQueryRepository;

    @Autowired
    DisciplineCommandService sut;

    private User user;
    private Artist artist;

    @BeforeEach
    void setUp() {
        user = userCommandRepository.save(UserFixture.create());
        artist = artistCommandRepository.save(ArtistFixture.create());
    }

    @Test
    void 조치를_저장해야_한다() throws Exception {
        // given
        DisciplineCreateRequest request = new DisciplineCreateRequest(
                user.getId(),
                artist.getId(),
                DisciplineReason.COMMERCIAL_ADS,
                DisciplineType.ALL_3MONTHS,
                LocalDateTime.now()
        );

        // when
        Discipline discipline = sut.create(request);

        // then
        Optional<Discipline> result = disciplineQueryRepository.findById(discipline.getId());
        assertAll(
                () -> assertThat(result.isPresent()),
                () -> assertThat(result.get().getId().equals(discipline.getId())),
                () -> assertThat(result.get().getUser()).isEqualTo(discipline.getUser()),
                () -> assertThat(result.get().getArtist()).isEqualTo(discipline.getArtist()),
                () -> assertThat(result.get().getReason()).isEqualTo(discipline.getReason()),
                () -> assertThat(result.get().getType()).isEqualTo(discipline.getType()),
                () -> assertThat(result.get().getStartTime()).isEqualTo(discipline.getStartTime()),
                () -> assertThat(result.get().getEndTime()).isEqualTo(discipline.getEndTime())
        );
    }

    @Test
    void 해당하는_가수가_null이더라도_정상적으로_조치를_저장해야_한다() throws Exception {
        // given
        DisciplineCreateRequest request = new DisciplineCreateRequest(
                user.getId(),
                null,
                DisciplineReason.COMMERCIAL_ADS,
                DisciplineType.ALL_3MONTHS,
                LocalDateTime.now()
        );

        // when
        Discipline discipline = sut.create(request);

        // then
        Optional<Discipline> result = disciplineQueryRepository.findById(discipline.getId());
        assertAll(
                () -> assertThat(result.isPresent()),
                () -> assertThat(result.get().getId().equals(discipline.getId())),
                () -> assertThat(result.get().getUser()).isEqualTo(discipline.getUser()),
                () -> assertThat(result.get().getArtist()).isEqualTo(discipline.getArtist()),
                () -> assertThat(result.get().getReason()).isEqualTo(discipline.getReason()),
                () -> assertThat(result.get().getType()).isEqualTo(discipline.getType()),
                () -> assertThat(result.get().getStartTime()).isEqualTo(discipline.getStartTime()),
                () -> assertThat(result.get().getEndTime()).isEqualTo(discipline.getEndTime())
        );
    }

    @Test
    void 조치의_종류가_강제탈퇴일_경우_해당_사용자를_강제_탈퇴시켜야_한다() {
        // given
        Long userId = user.getId();
        DisciplineCreateRequest request = new DisciplineCreateRequest(
                user.getId(),
                null,
                DisciplineReason.COMMERCIAL_ADS,
                DisciplineType.FORCED_WITHDRAWAL,
                LocalDateTime.now()
        );

        // when
        sut.create(request);

        //then
        assertAll(
                () -> assertThat(userQueryRepository.findById(userId).isEmpty())
        );
    }
}
