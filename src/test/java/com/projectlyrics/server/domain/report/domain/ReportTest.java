package com.projectlyrics.server.domain.report.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.report.dto.request.ReportResolveRequest;
import com.projectlyrics.server.domain.report.exception.ReportTargetConfilctedException;
import com.projectlyrics.server.domain.report.exception.ReportTargetMissingException;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.CommentFixture;
import com.projectlyrics.server.support.fixture.NoteFixture;
import com.projectlyrics.server.support.fixture.ReportFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;

public class ReportTest {

    @Test
    void ReportCreate_객체로부터_노트에_대한_Report_객체를_생성할_수_있다() {
        // given
        User reporter = UserFixture.create();
        Note note = NoteFixture.create(reporter, SongFixture.create(ArtistFixture.create()));
        ReportReason reportReason = ReportReason.DEFAMATION;
        String email = "example@example.com";

        ReportCreate reportCreate = new ReportCreate(
                reporter,
                note,
                null,
                reportReason,
                email
        );

        // when
        Report report = Report.create(reportCreate);

        // then
        assertAll(
                () -> assertThat(report.getReporter().getId()).isEqualTo(reporter.getId()),
                () -> assertThat(report.getNote().getId()).isEqualTo(note.getId()),
                () -> assertThat(report.getComment()).isNull(),
                () -> assertThat(report.getReportReason()).isEqualTo(reportReason),
                () -> assertThat(report.getEmail()).isEqualTo(email),
                () -> assertThat(report.getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING),
                () -> assertThat(report.getIsFalseReport()).isEqualTo(Boolean.FALSE)
        );
    }

    @Test
    void ReportCreate_객체로부터_댓글에_대한_Report_객체를_생성할_수_있다() {
        // given
        User reporter = UserFixture.create();
        Note note = NoteFixture.create(reporter, SongFixture.create(ArtistFixture.create()));
        Comment comment = CommentFixture.create(note, UserFixture.create());
        ReportReason reportReason = ReportReason.DEFAMATION;
        String email = "example@example.com";

        ReportCreate reportCreate = new ReportCreate(
                reporter,
                null,
                comment,
                reportReason,
                email
        );

        // when
        Report report = Report.create(reportCreate);

        // then
        assertAll(
                () -> assertThat(report.getReporter().getId()).isEqualTo(reporter.getId()),
                () -> assertThat(report.getNote()).isNull(),
                () -> assertThat(report.getComment().getId()).isEqualTo(comment.getId()),
                () -> assertThat(report.getReportReason()).isEqualTo(reportReason),
                () -> assertThat(report.getEmail()).isEqualTo(email),
                () -> assertThat(report.getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING),
                () -> assertThat(report.getIsFalseReport()).isEqualTo(Boolean.FALSE)
        );
    }

    @Test
    void Report_객체에_Note와_Comment가_동시에_입력될_수_없다() {
        // given
        User reporter = UserFixture.create();
        Note note = NoteFixture.create(reporter, SongFixture.create(ArtistFixture.create()));
        Comment comment = CommentFixture.create(note, UserFixture.create());
        ReportReason reportReason = ReportReason.DEFAMATION;
        String email = "example@example.com";

        ReportCreate reportCreate = new ReportCreate(
                reporter,
                note,
                comment,
                reportReason,
                email
        );

        // when, then
        assertThatThrownBy(() -> Report.create(reportCreate))
                .isInstanceOf(ReportTargetConfilctedException.class);
    }

    @Test
    void Report_객체에_Note와_Comment_중_하나는_입력되어야_한다() {
        // given
        User reporter = UserFixture.create();
        ReportReason reportReason = ReportReason.DEFAMATION;
        String email = "example@example.com";

        ReportCreate reportCreate = new ReportCreate(
                reporter,
                null,
                null,
                reportReason,
                email
        );

        // when, then
        assertThatThrownBy(() -> Report.create(reportCreate))
                .isInstanceOf(ReportTargetMissingException.class);
    }

    @Test
    void Report의_ApprovalStatus와_isFalseReport를_수정한다() {
        // given
        User reporter = UserFixture.create();
        Note note = NoteFixture.create(reporter, SongFixture.create(ArtistFixture.create()));
        Report report = ReportFixture.create(note, reporter);
        ApprovalStatus approvalStatus = ApprovalStatus.DISMISSED;
        Boolean isFalseReport = Boolean.FALSE;

        // when
        report.resolve(ReportResolve.from(ReportResolveRequest.of(approvalStatus, isFalseReport)));

        // then
        assertAll(
                () -> assertThat(report.getReporter().getId()).isEqualTo(reporter.getId()),
                () -> assertThat(report.getApprovalStatus()).isEqualTo(approvalStatus),
                () -> assertThat(report.getIsFalseReport()).isEqualTo(isFalseReport)
        );
    }
}
