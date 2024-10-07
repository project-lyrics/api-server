package com.projectlyrics.server.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.dto.request.CommentCreateRequest;
import com.projectlyrics.server.domain.comment.dto.request.CommentUpdateRequest;
import com.projectlyrics.server.domain.comment.exception.InvalidCommentDeletionException;
import com.projectlyrics.server.domain.comment.exception.InvalidCommentUpdateException;
import com.projectlyrics.server.domain.comment.repository.CommentQueryRepository;
import com.projectlyrics.server.domain.discipline.exception.InvaildDisciplineActionException;
import com.projectlyrics.server.domain.discipline.repository.DisciplineCommandRepository;
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
import com.projectlyrics.server.support.fixture.DisciplineFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentCommandServiceTest extends IntegrationTest {

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
    CommentQueryRepository commentQueryRepository;

    @Autowired
    DisciplineCommandRepository disciplineCommandRepository;

    @Autowired
    CommentCommandService sut;


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
    void 댓글을_저장해야_한다() {
        // given
        CommentCreateRequest request = new CommentCreateRequest(
                "content",
                note.getId()
        );

        // when
        Comment comment = sut.create(request, user.getId());

        // then
        assertAll(
                () -> assertThat(comment.getContent()).isEqualTo(request.content()),
                () -> assertThat(comment.getWriter()).isEqualTo(user),
                () -> assertThat(comment.getNote()).isEqualTo(note)
        );
    }

    @Test
    void 댓글을_수정해야_한다() {
        // given
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest(
                "content",
                note.getId()
        );
        Comment comment = sut.create(commentCreateRequest, user.getId());

        CommentUpdateRequest updateRequest = new CommentUpdateRequest("new content");

        // when
        Comment updatedComment = sut.update(updateRequest, comment.getId(), user.getId());

        // then
        assertAll(
                () -> assertThat(updatedComment.getContent()).isEqualTo(updateRequest.content()),
                () -> assertThat(updatedComment.getWriter()).isEqualTo(user),
                () -> assertThat(updatedComment.getNote()).isEqualTo(note)
        );
    }

    @Test
    void 댓글의_작성자가_아닌_경우_수정할_수_없어야_한다() {
        // given
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest(
                "content",
                note.getId()
        );
        Comment comment = sut.create(commentCreateRequest, user.getId());

        User anonymousUser = userCommandRepository.save(UserFixture.create());
        CommentUpdateRequest updateRequest = new CommentUpdateRequest("new content");

        // when, then
        assertThatThrownBy(() -> sut.update(updateRequest, comment.getId(), anonymousUser.getId()))
                .isInstanceOf(InvalidCommentUpdateException.class);
    }

    @Test
    void 댓글을_soft_delete_방식으로_삭제해야_한다() {
        // given
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest(
                "content",
                note.getId()
        );
        Comment comment = sut.create(commentCreateRequest, user.getId());

        // when
        sut.delete(comment.getId(), user.getId());

        // then
        assertThat(commentQueryRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    void 댓글의_작성자가_아닌_경우_삭제할_수_없어야_한다() {
        // given
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest(
                "content",
                note.getId()
        );
        Comment comment = sut.create(commentCreateRequest, user.getId());

        User anonymousUser = userCommandRepository.save(UserFixture.create());

        // when, then
        assertThatThrownBy(() -> sut.delete(comment.getId(), anonymousUser.getId()))
                .isInstanceOf(InvalidCommentDeletionException.class);
    }

    @Test
    void 작성자가_전체_글쓰기_제한_징계에_걸려_있다면_댓글을_생성할_수_없다() {
        // given
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest(
                "content",
                note.getId()
        );
        disciplineCommandRepository.save(DisciplineFixture.createForAll(artist, user));

        // when, then
        assertThatThrownBy(() -> sut.create(commentCreateRequest, user.getId()))
                .isInstanceOf(InvaildDisciplineActionException.class);
    }

    @Test
    void 작성자가_해당_아티스트에_대한_글쓰기_제한_징계에_걸려_있다면_댓글을_생성할_수_없다() {
        // given
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest(
                "content",
                note.getId()
        );
        disciplineCommandRepository.save(DisciplineFixture.createForArtist(artist, user));

        // when, then
        assertThatThrownBy(() -> sut.create(commentCreateRequest, user.getId()))
                .isInstanceOf(InvaildDisciplineActionException.class);
    }
}