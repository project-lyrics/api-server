package com.projectlyrics.server.domain.report.repository;

import com.projectlyrics.server.domain.report.domain.Report;
import java.util.Optional;

public interface ReportQueryRepository {

    Optional<Report> findById(Long reportId);
    Optional<Report> findByReporterIdAndNoteIdAndCommentId(Long reporterId, Long noteId, Long commentId);
}
