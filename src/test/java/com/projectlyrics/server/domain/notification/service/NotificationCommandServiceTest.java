package com.projectlyrics.server.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.domain.CommentCreate;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

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

    @MockBean
    FirebaseMessaging firebaseMessaging;

    @Autowired
    NotificationCommandService sut;

    private Comment comment;
    private User user;
    private List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 10; i++) {
            users.add(userCommandRepository.save(UserFixture.create()));
        }
        user = users.getFirst();

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
    void 댓글에_대한_알림을_저장한다() throws Exception {
        // given
        CommentEvent commentEvent = CommentEvent.from(comment);

        // when
        sut.createCommentNotification(commentEvent);
        sut.createCommentNotification(commentEvent);
        sut.createCommentNotification(commentEvent);

        // then
        List<Notification> result = notificationQueryRepository.findAllByReceiverId(user.getId(), null, PageRequest.ofSize(10))
                .getContent();

        assertAll(
                () -> assertThat(result.getFirst().getType()).isEqualTo(NotificationType.COMMENT_ON_NOTE),
                () -> assertThat(result.getFirst().getSender()).isEqualTo(comment.getWriter()),
                () -> assertThat(result.getFirst().getReceiver()).isEqualTo(comment.getNote().getPublisher()),
                () -> assertThat(result.getFirst().getNote()).isEqualTo(comment.getNote()),
                () -> assertThat(result.getFirst().getComment()).isEqualTo(comment)
        );
    }

    @Test
    void 공개_알림을_저장한다() throws Exception {
        // given
        String content = "content";

        // when
        sut.createPublicNotification(user.getId(), content);

        // then
        List<Notification> result = notificationQueryRepository.findAllBySenderId(user.getId(), null, PageRequest.ofSize(10))
                .getContent();

        assertAll(
                () -> assertThat(result.size()).isEqualTo(10),
                () -> assertThat(result.stream().allMatch(notification -> notification.getType().equals(NotificationType.PUBLIC))).isTrue(),
                () -> assertThat(result.stream().allMatch(notification -> notification.getSender().getId().equals(user.getId()))).isTrue(),
                () -> assertThat(result.stream().allMatch(notification -> notification.getContent().equalsIgnoreCase(content))).isTrue()
        );
    }
}