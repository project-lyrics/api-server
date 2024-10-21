package com.projectlyrics.server.domain.song.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.common.dto.util.OffsetBasePaginatedResponse;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteCreate;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.repository.NoteCommandRepository;
import com.projectlyrics.server.domain.song.dto.request.SongCreateRequest;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.song.dto.response.SongSearchResponse;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class SongQueryServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongCommandService songCommandService;

    @Autowired
    NoteCommandRepository noteCommandRepository;

    @Autowired
    SongQueryService sut;

    private User user;
    private Artist artist1;
    private Artist artist2;
    private SongCreateRequest requestOfArtist1;
    private SongCreateRequest requestOfArtist2;
    private SongCreateRequest request2OfArtist2;

    @BeforeEach
    void setUp() {
        user = userCommandRepository.save(UserFixture.create());

        artist1 = artistCommandRepository.save(ArtistFixture.create());
        artist2 = artistCommandRepository.save(ArtistFixture.create());

        requestOfArtist1 = new SongCreateRequest(
                1L,
                "spotifyId",
                "Kiss And Tell",
                LocalDate.now(),
                "albumName",
                "imageUrl",
                artist1.getId()
        );
        requestOfArtist2 = new SongCreateRequest(
                2L,
                "spotifyId",
                "Kiss And Tell",
                LocalDate.now(),
                "albumName",
                "imageUrl",
                artist2.getId()
        );
        request2OfArtist2 = new SongCreateRequest(
                3L,
                "spotifyId",
                "Kiss And Tell",
                LocalDate.now(),
                "albumName",
                "imageUrl",
                artist2.getId()
        );
    }

    @ValueSource(strings = {"Kiss", "Tell"})
    @ParameterizedTest
    void 검색어를_기반으로_제목이_일치하는_곡을_조회해야_한다(String query) {
        // given
        SongCreateRequest request = new SongCreateRequest(
                1L,
                "spotifyId",
                "Kiss And Tell",
                LocalDate.now(),
                "albumName",
                "imageUrl",
                artist1.getId()
        );
        Song song = songCommandService.create(request);

        // when
        OffsetBasePaginatedResponse<SongSearchResponse> result = sut.searchSongs(query, 0, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(1),
                () -> assertThat(result.data().getFirst().id()).isEqualTo(song.getId()),
                () -> assertThat(result.data().getFirst().name()).isEqualTo(song.getName()),
                () -> assertThat(result.data().getFirst().imageUrl()).isEqualTo(song.getImageUrl()),
                () -> assertThat(result.data().getFirst().artist().id()).isEqualTo(artist1.getId()),
                () -> assertThat(result.data().getFirst().artist().name()).isEqualTo(artist1.getName()),
                () -> assertThat(result.data().getFirst().artist().imageUrl()).isEqualTo(artist1.getImageUrl())
        );
    }

    @Test
    void 곡_리스트를_노트_개수_순서대로_조회해야_한다() {
        // given
        Song song1 = songCommandService.create(requestOfArtist1);
        Song song2 = songCommandService.create(requestOfArtist2);
        Song song3 = songCommandService.create(request2OfArtist2);

        NoteCreateRequest requestOfSong1 = new NoteCreateRequest(
                "data",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                song1.getId()
        );
        NoteCreateRequest requestOfSong2 = new NoteCreateRequest(
                "data",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                song2.getId()
        );
        NoteCreateRequest requestOfSong3 = new NoteCreateRequest(
                "data",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                song3.getId()
        );

        noteCommandRepository.save(Note.create(NoteCreate.from(requestOfSong3, user, song3)));
        noteCommandRepository.save(Note.create(NoteCreate.from(requestOfSong3, user, song3)));
        noteCommandRepository.save(Note.create(NoteCreate.from(requestOfSong3, user, song3)));

        noteCommandRepository.save(Note.create(NoteCreate.from(requestOfSong1, user, song1)));
        noteCommandRepository.save(Note.create(NoteCreate.from(requestOfSong1, user, song1)));

        noteCommandRepository.save(Note.create(NoteCreate.from(requestOfSong2, user, song2)));

        // when
        OffsetBasePaginatedResponse<SongSearchResponse> result = sut.searchSongs(null, 0, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(3),
                () -> assertThat(result.data().get(0).id()).isEqualTo(song3.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(song1.getId()),
                () -> assertThat(result.data().get(2).id()).isEqualTo(song2.getId())
        );
    }

    @ValueSource(strings = {"Kiss", "Tell"})
    @ParameterizedTest
    void 아티스트_id를_기반으로_제목이_일치하는_곡을_조회해야_한다(String query) {
        // given
        songCommandService.create(requestOfArtist1);
        songCommandService.create(requestOfArtist2);

        // when
        CursorBasePaginatedResponse<SongGetResponse> result = sut.searchSongsByArtist(artist1.getId(), query, 0L, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(1),
                () -> assertThat(result.data().getFirst().name()).isEqualTo("Kiss And Tell"),
                () -> assertThat(result.data().getFirst().artist().id()).isEqualTo(artist1.getId())
        );
    }

    @Test
    void 아티스트_id를_기반으로_곡_리스트를_id_역순으로_조회해야_한다() {
        // given
        for (int i = 0; i < 5; i++) {
            SongCreateRequest request = new SongCreateRequest(
                    (long) (i + 1),
                    "spotifyId",
                    "Kiss And Tell",
                    LocalDate.now(),
                    "albumName",
                    "imageUrl",
                    artist1.getId()
            );
            songCommandService.create(request);
        }

        // when
        CursorBasePaginatedResponse<SongGetResponse> result = sut.searchSongsByArtist(artist1.getId(), null, 0L, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(5),
                () -> assertThat(result.data().get(0).id()).isEqualTo(5),
                () -> assertThat(result.data().get(1).id()).isEqualTo(4),
                () -> assertThat(result.data().get(2).id()).isEqualTo(3),
                () -> assertThat(result.data().get(3).id()).isEqualTo(2),
                () -> assertThat(result.data().get(4).id()).isEqualTo(1)
        );
    }

    @Test
    void 곡_id를_기반으로_곡_관련_정보를_조회해야_한다() {
        // given
        Song song = songCommandService.create(requestOfArtist1);

        NoteCreateRequest request = new NoteCreateRequest(
                "data",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                song.getId()
        );

        noteCommandRepository.save(Note.create(NoteCreate.from(request, user, song)));
        noteCommandRepository.save(Note.create(NoteCreate.from(request, user, song)));

        // when
        SongSearchResponse result = sut.getById(song.getId());

        // then
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(song.getId()),
                () -> assertThat(result.name()).isEqualTo(song.getName()),
                () -> assertThat(result.imageUrl()).isEqualTo(song.getImageUrl()),
                () -> assertThat(result.noteCount()).isEqualTo(2L),
                () -> assertThat(result.artist().id()).isEqualTo(artist1.getId()),
                () -> assertThat(result.artist().name()).isEqualTo(artist1.getName()),
                () -> assertThat(result.artist().imageUrl()).isEqualTo(artist1.getImageUrl())
        );
    }
}