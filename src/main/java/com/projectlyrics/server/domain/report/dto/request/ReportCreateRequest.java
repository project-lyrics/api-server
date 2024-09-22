package com.projectlyrics.server.domain.report.dto.request;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.report.domain.ReportReason;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ReportCreateRequest (

        Long noteId,

        Long commentId,

        @NotNull
        ReportReason reportReason,

        String detailedReportReason,
        
        @Email
        String email
){
}
