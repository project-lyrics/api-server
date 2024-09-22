package com.projectlyrics.server.domain.report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.repository.CommentCommandRepository;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteCreate;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.repository.NoteCommandRepository;
import com.projectlyrics.server.domain.report.domain.ApprovalStatus;
import com.projectlyrics.server.domain.report.domain.Report;
import com.projectlyrics.server.domain.report.domain.ReportReason;
import com.projectlyrics.server.domain.report.dto.request.ReportCreateRequest;
import com.projectlyrics.server.domain.report.dto.request.ReportResolveRequest;
import com.projectlyrics.server.domain.report.exception.DuplicateReportException;
import com.projectlyrics.server.domain.report.repository.ReportCommandRepository;
import com.projectlyrics.server.domain.report.repository.ReportQueryRepository;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.global.slack.SlackClient;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.CommentFixture;
import com.projectlyrics.server.support.fixture.ReportFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class ReportCommandServiceTest extends IntegrationTest {

    @MockBean
    private SlackClient slackClient;

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
    ReportCommandRepository reportCommandRepository;

    @Autowired
    ReportQueryRepository reportQueryRepository;

    @Autowired
    ReportCommandService sut;

    private User user;
    private Artist artist;
    private Song song;
    private Note note;
    private Comment comment;

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
        comment = commentCommandRepository.save(CommentFixture.create(note,user));
        doNothing().when(slackClient).sendNoteReportMessage(any());
        doNothing().when(slackClient).sendCommentReportMessage(any());
    }

    @Test
    void 노트에_대한_신고를_저장해야_한다() throws Exception {
        // given
        ReportCreateRequest request = new ReportCreateRequest(
                note.getId(),
                null,
                ReportReason.POLITICAL_RELIGIOUS,
                "example@example.com"
        );

        // when
        Report report = sut.create(request, user.getId());

        // then
        verify(slackClient).sendNoteReportMessage(report);
        Optional<Report> result = reportQueryRepository.findById(report.getId());
        assertAll(
                () -> assertThat(result.isPresent()),
                () -> assertThat(result.get().getId().equals(report.getId())),
                () -> assertThat(result.get().getReporter().getId().equals(report.getReporter().getId())),
                () -> assertThat(result.get().getNote().getId().equals(report.getNote().getId())),
                () -> assertThat(result.get().getComment()).isNull(),
                () -> assertThat(result.get().getReportReason().equals(report.getReportReason())),
                () -> assertThat(result.get().getEmail().equals(report.getEmail())),
                () -> assertThat(result.get().getApprovalStatus().equals(report.getApprovalStatus())),
                () -> assertThat(result.get().getIsFalseReport().equals(report.getIsFalseReport()))

        );
    }

    @Test
    void 댓글에_대한_신고를_저장해야_한다() throws Exception {
        // given
        ReportCreateRequest request = new ReportCreateRequest(
                null,
                comment.getId(),
                ReportReason.POLITICAL_RELIGIOUS,
                "example@example.com"
        );

        // when
        Report report = sut.create(request, user.getId());

        // then
        verify(slackClient).sendCommentReportMessage(report);
        Optional<Report> result = reportQueryRepository.findById(report.getId());
        assertAll(
                () -> assertThat(result.isPresent()),
                () -> assertThat(result.get().getId().equals(report.getId())),
                () -> assertThat(result.get().getReporter().getId().equals(report.getReporter().getId())),
                () -> assertThat(result.get().getNote()).isNull(),
                () -> assertThat(result.get().getComment().getId().equals(report.getComment().getId())),
                () -> assertThat(result.get().getReportReason().equals(report.getReportReason())),
                () -> assertThat(result.get().getEmail().equals(report.getEmail())),
                () -> assertThat(result.get().getApprovalStatus().equals(report.getApprovalStatus())),
                () -> assertThat(result.get().getIsFalseReport().equals(report.getIsFalseReport()))
                );
    }

    @Test
    void 같은_신고자와_같은_대상으로_한_신고가_이미_존재한다면_오류를_발생한다() throws Exception {
        // given
        ReportCreateRequest request = new ReportCreateRequest(
                note.getId(),
                null,
                ReportReason.OTHER,
                "example@example.com"
        );
        sut.create(request, user.getId());

        // when, then
        assertThatThrownBy(() -> sut.create(request, user.getId()))
                .isInstanceOf(DuplicateReportException.class);
    }

    @Test
    void 기존_신고에_대해_승인_상태_허위_신고_여부를_수정해야_한다() throws Exception {
        // given
        Report report = reportCommandRepository.save(ReportFixture.create(note,user));
        ReportResolveRequest request = new ReportResolveRequest(
                ApprovalStatus.ACCEPTED,
                Boolean.TRUE
        );

        // when
        Long reportId = sut.resolve(request, report.getId());

        // then
        Optional<Report> result = reportQueryRepository.findById(reportId);
        assertAll(
                () -> assertThat(result.isPresent()),
                () -> assertThat(result.get().getId().equals(report.getId())),
                () -> assertThat(result.get().getReporter().getId().equals(report.getReporter().getId())),
                () -> assertThat(result.get().getNote().getId().equals(report.getNote().getId())),
                () -> assertThat(result.get().getComment()).isNull(),
                () -> assertThat(result.get().getReportReason().equals(report.getReportReason())),
                () -> assertThat(result.get().getEmail().equals(report.getEmail())),
                () -> assertThat(result.get().getApprovalStatus().equals(request.approvalStatus())),
                () -> assertThat(result.get().getIsFalseReport().equals(request.isFalseReport()))

        );
    }

    @Test
    void 신고를_승인하면_해당_신고의_대상인_노트가_삭제된다() throws Exception {
        // given
        Report report = reportCommandRepository.save(ReportFixture.create(note,user));
        ReportResolveRequest request = new ReportResolveRequest(
                ApprovalStatus.ACCEPTED,
                Boolean.TRUE
        );

        // when
        sut.resolve(request, report.getId());

        // then
        assertAll(
                () -> assertThat(noteCommandRepository.findById(note.getId()).isEmpty())
        );
    }

    @Test
    void 신고를_승인하면_해당_신고의_대상인_댓글이_삭제된다() throws Exception {
        // given
        Report report = reportCommandRepository.save(ReportFixture.create(comment,user));
        ReportResolveRequest request = new ReportResolveRequest(
                ApprovalStatus.ACCEPTED,
                Boolean.TRUE
        );

        // when
        sut.resolve(request, report.getId());

        // then
        assertAll(
                () -> assertThat(commentCommandRepository.findById(comment.getId()).isEmpty())
        );
    }
}
