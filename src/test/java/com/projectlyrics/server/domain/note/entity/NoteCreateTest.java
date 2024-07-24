package com.projectlyrics.server.domain.note.entity;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.global.exception.DomainNullFieldException;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class NoteCreateTest {

    @Test
    void 요청_파라미터와_작성자_및_곡_객체로부터_노트_생성_객체를_생성해야_한다() {
        // given
        Artist artist = ArtistFixture.create();
        User publisher = UserFixture.create();
        Song song = SongFixture.create(artist);
        NoteCreateRequest request = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.WHITE,
                NoteStatus.PUBLISHED,
                publisher.getId(),
                song.getId()
        );

        // when
        NoteCreate result = NoteCreate.from(request, publisher, song);

        // then
        assertAll(
                () -> assertThat(result.content()).isEqualTo(request.content()),
                () -> assertThat(result.lyrics()).isEqualTo(request.lyrics()),
                () -> assertThat(result.background()).isEqualTo(request.background()),
                () -> assertThat(result.status()).isEqualTo(request.status()),
                () -> assertThat(result.publisher()).isEqualTo(publisher),
                () -> assertThat(result.song()).isEqualTo(song)
        );
    }

    @Test
    void 널_노트_배경색에_대해_예외를_발생시켜야_한다() {
        // given
        Artist artist = ArtistFixture.create();
        User publisher = UserFixture.create();
        Song song = SongFixture.create(artist);
        NoteCreateRequest request = new NoteCreateRequest(
                "content",
                "lyrics",
                null,
                NoteStatus.PUBLISHED,
                publisher.getId(),
                song.getId()
        );

        // when & then
        assertThatThrownBy(() -> NoteCreate.from(request, publisher, song))
                .isInstanceOf(DomainNullFieldException.class);
    }

    @Test
    void 널_노트_상태에_대해_예외를_발생시켜야_한다() {
        // given
        Artist artist = ArtistFixture.create();
        User publisher = UserFixture.create();
        Song song = SongFixture.create(artist);
        NoteCreateRequest request = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.WHITE,
                null,
                publisher.getId(),
                song.getId()
        );

        // when & then
        assertThatThrownBy(() -> NoteCreate.from(request, publisher, song))
                .isInstanceOf(DomainNullFieldException.class);
    }

    @Test
    void 널_작성자에_대해_예외를_발생시켜야_한다() {
        // given
        Artist artist = ArtistFixture.create();
        User publisher = null;
        Song song = SongFixture.create(artist);
        NoteCreateRequest request = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.WHITE,
                NoteStatus.PUBLISHED,
                1L,
                song.getId()
        );

        // when & then
        assertThatThrownBy(() -> NoteCreate.from(request, publisher, song))
                .isInstanceOf(DomainNullFieldException.class);
    }

    @Test
    void 널_곡에_대해_예외를_발생시켜야_한다() {
        // given
        Artist artist = ArtistFixture.create();
        User publisher = UserFixture.create();
        Song song = null;
        NoteCreateRequest request = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.WHITE,
                NoteStatus.PUBLISHED,
                publisher.getId(),
                1L
        );

        // when & then
        assertThatThrownBy(() -> NoteCreate.from(request, publisher, song))
                .isInstanceOf(DomainNullFieldException.class);
    }
}