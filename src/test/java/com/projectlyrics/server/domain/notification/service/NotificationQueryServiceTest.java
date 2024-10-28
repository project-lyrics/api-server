package com.projectlyrics.server.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.comment.dto.request.CommentCreateRequest;
import com.projectlyrics.server.domain.comment.service.CommentCommandService;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteCreate;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.repository.NoteCommandRepository;
import com.projectlyrics.server.domain.notification.api.dto.response.NotificationGetResponse;
import com.projectlyrics.server.domain.notification.repository.NotificationCommandRepository;
import com.projectlyrics.server.domain.notification.repository.NotificationQueryRepository;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.projectlyrics.server.domain.notification.domain.NotificationType.COMMENT_ON_NOTE;
import static com.projectlyrics.server.domain.notification.domain.NotificationType.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NotificationQueryServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongCommandRepository songCommandRepository;

    @Autowired
    NoteCommandRepository noteCommandRepository;

    @Autowired
    CommentCommandService commentCommandService;

    @Autowired
    NotificationCommandRepository notificationCommandRepository;

    @Autowired
    NotificationQueryRepository notificationQueryRepository;

    @MockBean
    FirebaseMessaging firebaseMessaging;

    @Autowired
    NotificationCommandService notificationCommandService;

    @Autowired
    NotificationQueryService sut;

    private User user;
    private Note note;

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
        note = noteCommandRepository.save(Note.create(NoteCreate.from(noteCreateRequest, user, song)));
    }

    @Test
    void 최근_전체_알림_목록을_읽을_수_있다() {
        // given
        int notificationSize = 10;
        String notificationContent = "content";

        for (int i = 0; i < notificationSize; i++) {
            notificationCommandService.createPublicNotification(user.getId(), notificationContent);
        }

        // when
        CursorBasePaginatedResponse<NotificationGetResponse> result = sut.getRecentNotifications(List.of(PUBLIC), user.getId(), null, 10);

        // then
        assertAll(
                () -> assertThat(result.data()).hasSize(notificationSize),
                () -> assertThat(result.data().stream().map(NotificationGetResponse::type)).allMatch(type -> type.equals(PUBLIC)),
                () -> assertThat(result.data().stream().map(NotificationGetResponse::content)).allMatch(content -> content.equals(notificationContent)),
                () -> assertThat(result.data().stream().map(NotificationGetResponse::checked)).allMatch(checked -> checked.equals(false))
        );
    }

    @Test
    void 최근_게시글_알림_목록을_읽을_수_있다() {
        // given
        int notificationSize = 10;

        for (int i = 0; i< notificationSize; i++) {
            commentCommandService.create(
                    new CommentCreateRequest(
                            "abcde",
                            note.getId()
                    ),
                    user.getId()
            );
        }

        // when
        CursorBasePaginatedResponse<NotificationGetResponse> result = sut.getRecentNotifications(List.of(COMMENT_ON_NOTE), user.getId(), null, 10);

        // then
        assertAll(
                () -> assertThat(result.data().stream().map(NotificationGetResponse::type)).allMatch(type -> type.equals(COMMENT_ON_NOTE)),
                () -> assertThat(result.data().stream().map(NotificationGetResponse::checked)).allMatch(checked -> checked.equals(false))
        );
    }

    @Test
    void 읽지_않은_알림이_있는지_확인할_수_있다() {
        // given
        int notificationSize = 10;
        for (int i = 0; i < notificationSize; i++) {
            notificationCommandService.createPublicNotification(user.getId(), "content");
        }

        CursorBasePaginatedResponse<NotificationGetResponse> response = sut.getRecentNotifications(List.of(PUBLIC), user.getId(), null, 10);
        for (int i = 0; i < notificationSize; i++) {
            notificationCommandService.check(response.data().get(i).id(), user.getId());
        }

        // when
        boolean result = sut.hasUnchecked(user.getId());

        // then
        assertThat(result).isFalse();
    }
}