package com.projectlyrics.server.domain.favoriteartist.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.favoriteartist.dto.response.FavoriteArtistResponse;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistCommandRepository;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.service.NoteCommandService;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.FavoriteArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class FavoriteArtistQueryServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    FavoriteArtistCommandRepository favoriteArtistCommandRepository;

    @Autowired
    SongCommandRepository songCommandRepository;

    @Autowired
    NoteCommandService noteCommandService;

    @Autowired
    FavoriteArtistQueryService sut;

    User user;
    Artist artist1;
    Artist artist2;
    NoteCreateRequest request1;
    NoteCreateRequest request2;

    @BeforeEach
    void setUp() {
        user = userCommandRepository.save(UserFixture.create());
        artist1 = artistCommandRepository.save(ArtistFixture.create());
        artist2 = artistCommandRepository.save(ArtistFixture.create());
        Song song1 = songCommandRepository.save(SongFixture.create(artist1));
        Song song2 = songCommandRepository.save(SongFixture.create(artist2));
        Song song3 = songCommandRepository.save(SongFixture.create(artist1));

        request1 = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                song1.getId()
        );

        request2 = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                song2.getId()
        );
    }

    @Test
    void 자신이_노트를_작성한_좋아하는_아티스트만_리스트로_조회할_수_있다() {
        // given
        favoriteArtistCommandRepository.save(FavoriteArtistFixture.create(user, artist1));
        favoriteArtistCommandRepository.save(FavoriteArtistFixture.create(user, artist2));

        noteCommandService.create(request1, user.getId());

        // when
        List<FavoriteArtistResponse> result = sut.findAllHavingNotesOfUser(user.getId());

        // then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(1),
                () -> assertThat(result.get(0).artist().id()).isEqualTo(1)
        );
    }
}