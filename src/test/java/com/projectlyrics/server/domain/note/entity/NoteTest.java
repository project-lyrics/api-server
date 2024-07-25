package com.projectlyrics.server.domain.note.entity;

import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
}