package com.projectlyrics.server.domain.notification.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.domain.CommentCreate;
import com.projectlyrics.server.domain.comment.dto.request.CommentCreateRequest;
import com.projectlyrics.server.domain.comment.repository.CommentCommandRepository;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteCreate;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.repository.NoteCommandRepository;
import com.projectlyrics.server.domain.notification.domain.Notification;
import com.projectlyrics.server.domain.notification.domain.NotificationType;
import com.projectlyrics.server.domain.notification.domain.event.CommentEvent;
import com.projectlyrics.server.domain.notification.repository.NotificationCommandRepository;
import com.projectlyrics.server.domain.notification.repository.NotificationQueryRepository;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NotificationCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongCommandRepository songCommandRepository;

    @Autowired
    NoteCommandRepository noteCommandRepository;

    @Autowired
    CommentCommandRepository commentCommandRepository;

    @Autowired
    NotificationCommandRepository notificationCommandRepository;

    @Autowired
    NotificationQueryRepository notificationQueryRepository;

    @Autowired
    NotificationCommandService sut;

    private Comment comment;
    private User user;

    @BeforeEach
    void setUp() {
        user = userCommandRepository.save(UserFixture.create());
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

        comment = commentCommandRepository.save(Comment.create(CommentCreate.of("content", user, note)));
    }

    @Test
    void 댓글에_대한_알림을_생성한다() {
        // given
        CommentEvent commentEvent = CommentEvent.from(comment);

        // when
        sut.createCommentNotification(commentEvent);

        // then
        List<Notification> result = notificationQueryRepository.findAllByReceiverId(user.getId(), null, PageRequest.ofSize(10))
                .getContent();

        assertAll(
                () -> assertThat(result.size()).isEqualTo(1),
                () -> assertThat(result.get(0).getType()).isEqualTo(NotificationType.COMMENT_ON_NOTE),
                () -> assertThat(result.get(0).getSender()).isEqualTo(comment.getWriter()),
                () -> assertThat(result.get(0).getReceiver()).isEqualTo(comment.getNote().getPublisher()),
                () -> assertThat(result.get(0).getNote()).isEqualTo(comment.getNote()),
                () -> assertThat(result.get(0).getComment()).isEqualTo(comment)
        );
    }
}