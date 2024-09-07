package com.projectlyrics.server.domain.report.domain;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.domain.CommentCreate;
import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.report.exception.ReportTargetConfilctedException;
import com.projectlyrics.server.domain.report.exception.ReportTargetMissingException;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Not;

@Getter
@Entity
@Table(name = "reports")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @Nullable
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @Nullable
    private Comment comment;

    @Enumerated(value = EnumType.STRING)
    private ReportReason reportReason;

    @Nullable
    private String email;

    @Enumerated(value = EnumType.STRING)
    private ApprovalStatus approvalStatus;

    private Boolean isFalseReport;

    private Report(Long id, User reporter, @Nullable Note note, @Nullable Comment comment, ReportReason reportReason,
                  @Nullable String email) {
        checkNoteOrComment(note,comment);
        this.id = id;
        this.reporter = reporter;
        this.note = note;
        this.comment = comment;
        this.reportReason = reportReason;
        this.email = email;
        this.approvalStatus = ApprovalStatus.PENDING;
        this.isFalseReport = false;
    }

    private Report(User reporter, @Nullable Note note, @Nullable Comment comment, ReportReason reportReason,
                   @Nullable String email) {
        this(null, reporter, note,comment,reportReason, email);
    }

    public static Report create(ReportCreate reportCreate) {
        return new Report(
                reportCreate.reporter(),
                reportCreate.note(),
                reportCreate.comment(),
                reportCreate.reportReason(),
                reportCreate.email()
        );
    }

    public static Report createWithId(Long id, ReportCreate reportCreate) {
        return new Report(
                id,
                reportCreate.reporter(),
                reportCreate.note(),
                reportCreate.comment(),
                reportCreate.reportReason(),
                reportCreate.email()
        );
    }

    private void checkNoteOrComment(Note note, Comment comment) {
        if (note == null && comment == null) {
            throw new ReportTargetMissingException();
        }
        if (note != null && comment != null) {
            throw new ReportTargetConfilctedException();
        }
    }

    public void setReportReason(ReportReason reportReason) {
        this.reportReason = reportReason;
        this.approvalStatus = ApprovalStatus.PENDING;
        this.isFalseReport = false;
    }

    public void resolve(ApprovalStatus approvalStatus, Boolean isFalseReport) {
        this.approvalStatus = approvalStatus;
        this.isFalseReport = isFalseReport;
    }
}
