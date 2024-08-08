package com.projectlyrics.server.domain.comment.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.dto.request.CommentCreateRequest;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
    CommentCommandService sut;

    @Test
    void 노트를_저장해야_한다() {
        // given
        User user = userCommandRepository.save(UserFixture.create());
        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        Song song = songCommandRepository.save(SongFixture.create(artist));
        NoteCreateRequest noteCreateRequest = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                song.getId()
        );
        Note note = noteCommandRepository.save(Note.create(NoteCreate.from(noteCreateRequest, user, song)));

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
}