package com.projectlyrics.server.domain.report.dto.request;

import com.projectlyrics.server.domain.report.domain.ReportReason;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ReportCreateRequest (

        Long noteId,

        Long commentId,

        @NotNull
        ReportReason reportReason,
        
        @Email
        String email
){
}
