package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.report.domain.Report;
import com.projectlyrics.server.domain.report.domain.ReportCreate;
import com.projectlyrics.server.domain.report.domain.ReportReason;
import com.projectlyrics.server.domain.user.entity.User;

public class ReportFixture extends BaseFixture{

    public static Report create(Note note,User reporter) {
        return Report.createWithId(
                getUniqueId(),
                ReportCreate.of(
                        reporter,
                        note,
                        null,
                        ReportReason.POLITICAL_RELIGIOUS,
                        "example@example.com"

                )
        );
    }

    public static Report create(Comment comment,User reporter) {
        return Report.createWithId(
                getUniqueId(),
                ReportCreate.of(
                        reporter,
                        null,
                        comment,
                        ReportReason.POLITICAL_RELIGIOUS,
                        "example@example.com"

                )
        );
    }
}
