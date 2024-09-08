package com.projectlyrics.server.domain.report.domain;

public record ReportResolve (
        ApprovalStatus approvalStatus,
        Boolean isFalseReport
){
    public static ReportResolve of(ApprovalStatus approvalStatus, Boolean isFalseReport) {
        return new ReportResolve(approvalStatus, isFalseReport);
    }
}
