package com.projectlyrics.server.domain.report.domain;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.annotation.Nullable;

public record ReportCreate (
        User reporter,
        Note note,
        Comment comment,
        ReportReason reportReason,

        String detailedReportReason,
        String email
) {
    public static ReportCreate of(User reporter, Note note, Comment comment, ReportReason reportReason, String detailedReportReason, String email) {
        return new ReportCreate(reporter, note, comment, reportReason, detailedReportReason, email);
    }
}
