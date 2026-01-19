package com.projectlyrics.server.domain.note.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;

class NoteTest {

    @Test
    void 가사가_없는_경우에도_노트를_생성해야_한다() {
        // given
        User publisher = UserFixture.create();
        Song song = SongFixture.create(ArtistFixture.create());
        NoteCreate noteCreate = new NoteCreate(
                "content",
                null,
                null,
                NoteStatus.PUBLISHED,
                NoteType.FREE,
                publisher,
                song
        );

        // when
        Note note = Note.create(noteCreate);

        // then
        assertAll(
                () -> assertThat(note.getContent()).isEqualTo("content"),
                () -> assertThat(note.getLyrics()).isNull(),
                () -> assertThat(note.getNoteStatus()).isEqualTo(NoteStatus.PUBLISHED),
                () -> assertThat(note.getNoteType()).isEqualTo(NoteType.FREE),
                () -> assertThat(note.getPublisher()).isEqualTo(publisher),
                () -> assertThat(note.getSong()).isEqualTo(song)
        );
    }

    @Test
    void 작성자_id와_임의의_사용자_id를_비교해야_한다() {
        // given
        User publisher = UserFixture.create();
        User unknownUser = UserFixture.create();
        Song song = SongFixture.create(ArtistFixture.create());

        NoteCreate noteCreate = new NoteCreate(
                "content",
                null,
                null,
                NoteStatus.PUBLISHED,
                NoteType.FREE,
                publisher,
                song
        );
        Note note = Note.create(noteCreate);

        // when, then
        assertAll(
                () -> assertThat(note.isPublisher(publisher.getId())).isTrue(),
                () -> assertThat(note.isPublisher(unknownUser.getId())).isFalse()
        );
    }

    @Test
    void 노트_수정_객체로부터_노트를_수정할_수_있다() {
        // given
        User publisher = UserFixture.create();
        Song song = SongFixture.create(ArtistFixture.create());
        NoteCreate noteCreate = new NoteCreate(
                "content",
                null,
                null,
                NoteStatus.DRAFT,
                NoteType.FREE,
                publisher,
                song
        );
        Note note = Note.create(noteCreate);

        NoteUpdate noteUpdate = new NoteUpdate(
                "updated content",
                "updated lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED
        );

        // when
        Note updatedNote = note.update(noteUpdate);

        // then
        assertAll(
                () -> assertThat(updatedNote.getContent()).isEqualTo("updated content"),
                () -> assertThat(updatedNote.getLyrics().getContent()).isEqualTo("updated lyrics"),
                () -> assertThat(updatedNote.getLyrics().getBackground()).isEqualTo(NoteBackground.DEFAULT),
                () -> assertThat(updatedNote.getNoteStatus()).isEqualTo(NoteStatus.PUBLISHED),
                () -> assertThat(updatedNote.getPublisher()).isEqualTo(publisher)
        );
    }

    @Test
    void 노트_수정_객체의_내용이_비었으면_수정하지_않는다() {
        // given
        User publisher = UserFixture.create();
        Song song = SongFixture.create(ArtistFixture.create());
        NoteCreate noteCreate = new NoteCreate(
                "content",
                null,
                null,
                NoteStatus.PUBLISHED,
                NoteType.FREE,
                publisher,
                song
        );
        Note note = Note.create(noteCreate);

        NoteUpdate noteUpdate = new NoteUpdate(
                "",
                "updated lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED
        );

        // when
        Note updatedNote = note.update(noteUpdate);

        // then
        assertThat(updatedNote.getContent()).isEqualTo("content");
    }

    @Test
    void 노트_수정_객체의_상태가_비었으면_수정하지_않는다() {
        // given
        User publisher = UserFixture.create();
        Song song = SongFixture.create(ArtistFixture.create());
        NoteCreate noteCreate = new NoteCreate(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                NoteType.FREE,
                publisher,
                song
        );
        Note note = Note.create(noteCreate);

        NoteUpdate noteUpdate = new NoteUpdate(
                "updated content",
                "updated lyrics",
                NoteBackground.DEFAULT,
                null
        );

        // when
        Note updatedNote = note.update(noteUpdate);

        // then
        assertThat(updatedNote.getNoteStatus()).isEqualTo(NoteStatus.PUBLISHED);
    }
}