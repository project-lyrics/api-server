package com.projectlyrics.server.domain.bookmark.service;


import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.bookmark.domain.Bookmark;
import com.projectlyrics.server.domain.bookmark.exception.BookmarkNotFoundException;
import com.projectlyrics.server.domain.bookmark.repository.BookmarkQueryRepository;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteCreate;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.repository.NoteCommandRepository;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class BookmarkCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongCommandRepository songCommandRepository;

    @Autowired
    NoteQueryRepository noteQueryRepository;

    @Autowired
    NoteCommandRepository noteCommandRepository;

    @Autowired
    BookmarkQueryRepository bookmarkQueryRepository;

    @Autowired
    BookmarkCommandService sut;

    private User user;
    private Artist artist;
    private Song song;
    private Note note;

    @BeforeEach
    void setUp() {
        user = userCommandRepository.save(UserFixture.create());
        artist = artistCommandRepository.save(ArtistFixture.create());
        song = songCommandRepository.save(SongFixture.create(artist));
        NoteCreateRequest noteCreateRequest = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                song.getId()
        );
        note = noteCommandRepository.save(Note.create(NoteCreate.from(noteCreateRequest, user, song)));
    }

    @Test
    void 북마크를_저장해야_한다() {
        // when
        Bookmark bookmark = sut.create(note.getId(), user.getId());

        // then
        assertAll(
                () -> assertThat(bookmark.getUser()).isEqualTo(user),
                () -> assertThat(bookmark.getNote()).isEqualTo(note)
        );
    }

    @Test
    void 북마크를_삭제해야_한다() {
        // given
        sut.create(note.getId(), user.getId());

        // when
        sut.delete(note.getId(), user.getId());

        // then
        assertThat(bookmarkQueryRepository.findByNoteIdAndUserId(note.getId(), user.getId())).isEmpty();
    }

    @Test
    void 존재하지_않는_북마크를_삭제하지_않고_예외가_발생해야_한다() {
        // when, then
        assertThatThrownBy(() -> sut.delete(99L, 99L))
                .isInstanceOf(BookmarkNotFoundException.class);
    }
}