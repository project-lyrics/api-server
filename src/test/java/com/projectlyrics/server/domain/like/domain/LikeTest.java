package com.projectlyrics.server.domain.like.domain;

import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.NoteFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LikeTest {

    private User user;
    private Note note;

    @BeforeEach
    void setUp() {
        user = UserFixture.create();
        note = NoteFixture.create(user, SongFixture.create(ArtistFixture.create()));
    }

    @Test
    void LikeCreate_객체로부터_Like_객체를_생성할_수_있다() {
        // given
        LikeCreate likeCreate = new LikeCreate(user, note);

        // when
        Like like = Like.create(likeCreate);

        // then
        assertAll(
                () -> assertThat(like.getUser()).isEqualTo(user),
                () -> assertThat(like.getNote()).isEqualTo(note)
        );
    }

    @Test
    void id와_LikeCreate_객체로부터_Like_객체를_생성할_수_있다() {
        // given
        Long id = 1L;
        LikeCreate likeCreate = new LikeCreate(user, note);

        // when
        Like like = Like.createWithId(id, likeCreate);

        // then
        assertAll(
                () -> assertThat(like.getId()).isEqualTo(id),
                () -> assertThat(like.getUser()).isEqualTo(user),
                () -> assertThat(like.getNote()).isEqualTo(note)
        );
    }

    @Test
    void 좋아요를_기입한_사용자가_맞는지_확인할_수_있다() {
        // given
        LikeCreate likeCreate = new LikeCreate(user, note);
        Like like = Like.create(likeCreate);

        // when
        boolean result = like.isUser(user);

        // then
        assertThat(result).isTrue();
    }
}