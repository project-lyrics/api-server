package com.projectlyrics.server.domain.report.dto.request;
import com.projectlyrics.server.domain.report.domain.ApprovalStatus;
import jakarta.validation.constraints.NotNull;

public record ReportResolveRequest(
        @NotNull
        ApprovalStatus approvalStatus,
        @NotNull
        Boolean isFalseReport
){
}