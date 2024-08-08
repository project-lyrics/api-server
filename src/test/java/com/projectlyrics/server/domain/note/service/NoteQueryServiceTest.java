package com.projectlyrics.server.domain.note.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistCommandRepository;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.dto.response.NoteGetResponse;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.FavoriteArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NoteQueryServiceTest extends IntegrationTest {

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
    NoteQueryService sut;

    @Test
    void 사용자_id와_일치하는_작성자와_연관된_노트_리스트를_최신순으로_조회해야_한다() {
        // given
        User user = userCommandRepository.save(UserFixture.create());
        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        Song song = songCommandRepository.save(SongFixture.create(artist));
        NoteCreateRequest request = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                song.getId()
        );

        Note note1 = noteCommandService.create(request, user.getId());
        Note note2 = noteCommandService.create(request, user.getId());
        Note note3 = noteCommandService.create(request, user.getId());
        Note note4 = noteCommandService.create(request, user.getId());

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getNotesByUserId(user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(4),
                () -> assertThat(result.data().get(0).id()).isEqualTo(note4.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(note3.getId()),
                () -> assertThat(result.data().get(2).id()).isEqualTo(note2.getId()),
                () -> assertThat(result.data().get(3).id()).isEqualTo(note1.getId())
        );
    }

    @Test
    void 사용자가_좋아하는_아티스트와_관련된_노트를_최신순으로_조회해야_한다() {
        // given
        User user = userCommandRepository.save(UserFixture.create());

        Artist unlikedArtist = artistCommandRepository.save(ArtistFixture.create());
        Song unlikedArtistSong = songCommandRepository.save(SongFixture.create(unlikedArtist));
        Artist likedArtist = artistCommandRepository.save(ArtistFixture.create());
        Song likedArtistSong = songCommandRepository.save(SongFixture.create(likedArtist));
        favoriteArtistCommandRepository.save(FavoriteArtistFixture.create(user, likedArtist));

        NoteCreateRequest unlikedArtistSongNoteRequest = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                unlikedArtistSong.getId()
        );

        NoteCreateRequest likedArtistSongNoteRequest = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                likedArtistSong.getId()
        );

        noteCommandService.create(unlikedArtistSongNoteRequest, user.getId());
        noteCommandService.create(unlikedArtistSongNoteRequest, user.getId());
        Note likedArtistSongNote1 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Note likedArtistSongNote2 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Note likedArtistSongNote3 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getNotesOfFavoriteArtists(user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(3),
                () -> assertThat(result.data().get(0).id()).isEqualTo(likedArtistSongNote3.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(likedArtistSongNote2.getId()),
                () -> assertThat(result.data().get(2).id()).isEqualTo(likedArtistSongNote1.getId())
        );
    }

    @Test
    void 특정_아티스트와_관련된_노트를_최신순으로_조회해야_한다() {
        // given
        User user = userCommandRepository.save(UserFixture.create());

        Artist artist1 = artistCommandRepository.save(ArtistFixture.create());
        Artist artist2 = artistCommandRepository.save(ArtistFixture.create());
        Song song = songCommandRepository.save(SongFixture.create(artist1));

        NoteCreateRequest request = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                song.getId()
        );

        Note note1 = noteCommandService.create(request, user.getId());
        Note note2 = noteCommandService.create(request, user.getId());
        Note note3 = noteCommandService.create(request, user.getId());

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result1 = sut.getNotesByArtistId(artist1.getId(), false, null, 5);
        CursorBasePaginatedResponse<NoteGetResponse> result2 = sut.getNotesByArtistId(artist2.getId(), false, null, 5);

        // then
        assertAll(
                () -> assertThat(result1.data().size()).isEqualTo(3),
                () -> assertThat(result1.data().get(0).id()).isEqualTo(note3.getId()),
                () -> assertThat(result1.data().get(1).id()).isEqualTo(note2.getId()),
                () -> assertThat(result1.data().get(2).id()).isEqualTo(note1.getId()),
                () -> assertThat(result2.data().size()).isEqualTo(0)
        );
    }

    @Test
    void 특정_아티스트와_관련된_노트_중_가사가_있는_것만_최신순으로_조회해야_한다() {
        // given
        User user = userCommandRepository.save(UserFixture.create());

        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        Song song = songCommandRepository.save(SongFixture.create(artist));

        NoteCreateRequest lyricsRequest = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                song.getId()
        );
        NoteCreateRequest noLyricsRequest = new NoteCreateRequest(
                "content",
                null,
                null,
                NoteStatus.PUBLISHED,
                song.getId()
        );

        Note note1 = noteCommandService.create(lyricsRequest, user.getId());
        Note note2 = noteCommandService.create(noLyricsRequest, user.getId());
        Note note3 = noteCommandService.create(lyricsRequest, user.getId());

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getNotesByArtistId(artist.getId(), true, null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(2),
                () -> assertThat(result.data().get(0).id()).isEqualTo(note3.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(note1.getId())
        );
    }
}