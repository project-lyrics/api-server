package com.projectlyrics.server.domain.report.dto.request;
import com.projectlyrics.server.domain.report.domain.ApprovalStatus;
import com.projectlyrics.server.domain.report.domain.ReportResolve;
import jakarta.validation.constraints.NotNull;

public record ReportResolveRequest(
        @NotNull
        ApprovalStatus approvalStatus,
        @NotNull
        Boolean isFalseReport
){
        public static ReportResolveRequest of(ApprovalStatus approvalStatus, Boolean isFalseReport) {
                return new ReportResolveRequest(approvalStatus, isFalseReport);
        }
}