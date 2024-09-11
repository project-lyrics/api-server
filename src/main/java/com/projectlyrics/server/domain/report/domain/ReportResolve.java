package com.projectlyrics.server.domain.report.domain;

import com.projectlyrics.server.domain.report.dto.request.ReportResolveRequest;

public record ReportResolve (
        ApprovalStatus approvalStatus,
        Boolean isFalseReport
){
    public static ReportResolve from(ReportResolveRequest request) {
        return new ReportResolve(request.approvalStatus(), request.isFalseReport());
    }
}
