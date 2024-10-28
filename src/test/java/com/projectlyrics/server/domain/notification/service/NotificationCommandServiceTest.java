package com.projectlyrics.server.domain.notification.service;

import static com.projectlyrics.server.domain.notification.domain.NotificationType.COMMENT_ON_NOTE;
import static com.projectlyrics.server.domain.notification.domain.NotificationType.DISCIPLINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.google.firebase.messaging.FirebaseMessaging;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.domain.CommentCreate;
import com.projectlyrics.server.domain.comment.repository.CommentCommandRepository;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.domain.discipline.domain.Discipline;
import com.projectlyrics.server.domain.discipline.domain.DisciplineCreate;
import com.projectlyrics.server.domain.discipline.domain.DisciplineReason;
import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import com.projectlyrics.server.domain.discipline.repository.DisciplineCommandRepository;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteCreate;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.repository.NoteCommandRepository;
import com.projectlyrics.server.domain.notification.api.dto.response.NotificationGetResponse;
import com.projectlyrics.server.domain.notification.domain.Notification;
import com.projectlyrics.server.domain.notification.domain.NotificationType;
import com.projectlyrics.server.domain.notification.domain.event.CommentEvent;
import com.projectlyrics.server.domain.notification.domain.event.DisciplineEvent;
import com.projectlyrics.server.domain.notification.exception.NotificationNotFoundException;
import com.projectlyrics.server.domain.notification.exception.NotificationReceiverUnmatchException;
import com.projectlyrics.server.domain.notification.repository.NotificationCommandRepository;
import com.projectlyrics.server.domain.notification.repository.NotificationQueryRepository;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;

class NotificationCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongCommandRepository songCommandRepository;

    @Autowired
    NoteCommandRepository noteCommandRepository;

    @Autowired
    CommentCommandRepository commentCommandRepository;

    @Autowired
    DisciplineCommandRepository disciplineCommandRepository;

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
    private Discipline discipline;

    @BeforeEach
    void setUp() {
        notificationCommandRepository.deleteAll();
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

        discipline = disciplineCommandRepository.save(Discipline.create(DisciplineCreate.of(user, artist, DisciplineReason.COMMERCIAL_ADS, DisciplineType.ALL_3MONTHS, LocalDateTime.now(), "사용자에게 갈 알림 메세지입니다")));

    }

    @Test
    void 댓글에_대한_알림을_저장한다() throws Exception {
        // given
        given(firebaseMessaging.send(any())).willReturn(null);
        CommentEvent commentEvent = CommentEvent.from(comment);

        // when
        sut.createCommentNotification(commentEvent).get();

        // then
        List<NotificationGetResponse> result = notificationQueryRepository.findAllByTypesAndReceiverId(List.of(COMMENT_ON_NOTE), user.getId(), null, 10)
                .getContent();

        assertAll(
                () -> assertThat(result.getFirst().type()).isEqualTo(COMMENT_ON_NOTE),
                () -> assertThat(result.getFirst().noteId()).isEqualTo(comment.getNote().getId()),
                () -> assertThat(result.getFirst().noteContent()).isEqualTo(comment.getNote().getContent()),
                () -> assertThat(result.getFirst().content()).isNull()
                );
    }

    @Test
    void 조치에_대한_알림을_저장한다() throws Exception {
        // given
        given(firebaseMessaging.send(any())).willReturn(null);
        DisciplineEvent disciplineEvent = DisciplineEvent.from(user, discipline);

        // when
        sut.createDisciplineNotification(disciplineEvent).get();

        // then
        List<NotificationGetResponse> result = notificationQueryRepository.findAllByTypesAndReceiverId(List.of(DISCIPLINE), user.getId(), null, 10)
                .getContent();

        assertAll(
                () -> assertThat(result.getFirst().type()).isEqualTo(NotificationType.DISCIPLINE),
                () -> assertThat(result.getFirst().noteId()).isNull(),
                () -> assertThat(result.getFirst().noteContent()).isNull()
                //() -> assertThat(result.getFirst().content()).isNull()
                //TODO : 나중에 content 정해지면 content도 함께 검사할 것
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
                () -> assertThat(result).isNotEmpty(),
                () -> assertThat(result.size()).isEqualTo(10),
                () -> assertThat(result.stream().allMatch(notification -> notification.getType().equals(NotificationType.PUBLIC))).isTrue(),
                () -> assertThat(result.stream().allMatch(notification -> notification.getSender().getId().equals(user.getId()))).isTrue(),
                () -> assertThat(result.stream().allMatch(notification -> notification.getContent().equalsIgnoreCase(content))).isTrue()
        );
    }

    @Test
    void 알림을_확인했다고_처리한다() {
        // given
        sut.createPublicNotification(user.getId(), "content");

        // when
        Notification result = notificationQueryRepository.findById(1L);
        assertThat(result.isChecked()).isFalse();

        User receiver = userQueryRepository.findById(result.getReceiver().getId()).get();
        result.check(receiver.getId());

        // then
        assertThat(result.isChecked()).isTrue();
    }

    @Test
    void 알림을_확인하는_사용자_id가_알림의_수신자_id와_다를_경우_예외가_발생한다() {
        // given
        sut.createPublicNotification(user.getId(), "content");
        Notification notification = notificationQueryRepository.findById(1L);

        // when, then
        assertThatThrownBy(() -> notification.check(99L))
                .isInstanceOf(NotificationReceiverUnmatchException.class)
                .hasMessage(ErrorCode.NOTIFICATION_RECEIVER_UNMATCH.getErrorMessage());
    }

    @Test
    void 존재하지_않는_알림에_대해_확인할_수_없다() {
        // when, then
        assertThatThrownBy(() -> sut.check(99L, 1L))
                .isInstanceOf(NotificationNotFoundException.class)
                .hasMessage(ErrorCode.NOTIFICATION_NOT_FOUND.getErrorMessage());
    }
}