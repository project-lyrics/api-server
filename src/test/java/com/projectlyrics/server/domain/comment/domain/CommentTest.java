package com.projectlyrics.server.domain.comment.domain;

import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.NoteFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CommentTest {

    @Test
    void CommentCreate_객체로부터_Comment_객체를_생성할_수_있다() {
        // given
        String content = "content";
        User writer = UserFixture.create();
        Note note = NoteFixture.create(writer, SongFixture.create(ArtistFixture.create()));

        CommentCreate commentCreate = new CommentCreate(
                content,
                writer,
                note
        );

        // when
        Comment comment = Comment.create(commentCreate);

        // then
        assertAll(
                () -> assertThat(comment.getContent()).isEqualTo(content),
                () -> assertThat(comment.getWriter()).isEqualTo(writer),
                () -> assertThat(comment.getNote()).isEqualTo(note)
        );
    }

    @Test
    void id와_CommentCreate_객체로부터_Comment_객체를_생성할_수_있다() {
        // given
        Long id = 1L;
        String content = "content";
        User writer = UserFixture.create();
        Note note = NoteFixture.create(writer, SongFixture.create(ArtistFixture.create()));

        CommentCreate commentCreate = new CommentCreate(
                content,
                writer,
                note
        );

        // when
        Comment comment = Comment.createWithId(id, commentCreate);

        // then
        assertAll(
                () -> assertThat(comment.getId()).isEqualTo(id),
                () -> assertThat(comment.getContent()).isEqualTo(content),
                () -> assertThat(comment.getWriter()).isEqualTo(writer),
                () -> assertThat(comment.getNote()).isEqualTo(note)
        );
    }

    @Test
    void 작성자가_일치하는지_확인해야_한다() {
        // given
        User writer = UserFixture.create();
        Comment comment = Comment.create(new CommentCreate(
                "content",
                writer,
                NoteFixture.create(writer, SongFixture.create(ArtistFixture.create()))
        ));

        User anotherUser = UserFixture.create();

        // when
        boolean isWriter = comment.isWriter(writer.getId());
        boolean isNotWriter = comment.isWriter(anotherUser.getId());

        // then
        assertAll(
                () -> assertThat(isWriter).isTrue(),
                () -> assertThat(isNotWriter).isFalse()
        );
    }

    @Test
    void CommentUpdate_객체로부터_다른_content를_가지는_새로운_Comment_객체를_생성할_수_있다() {
        // given
        Long id = 1L;
        String content = "content";
        User writer = UserFixture.create();
        Note note = NoteFixture.create(writer, SongFixture.create(ArtistFixture.create()));

        Comment comment = Comment.createWithId(id, new CommentCreate(
                content,
                writer,
                note
        ));

        String newContent = "new content";
        CommentUpdate commentUpdate = new CommentUpdate(
                newContent
        );

        // when
        Comment updatedComment = comment.update(commentUpdate);

        // then
        assertAll(
                () -> assertThat(updatedComment.getId()).isEqualTo(id),
                () -> assertThat(updatedComment.getContent()).isEqualTo(newContent),
                () -> assertThat(updatedComment.getWriter()).isEqualTo(writer),
                () -> assertThat(updatedComment.getNote()).isEqualTo(note)
        );
    }
}