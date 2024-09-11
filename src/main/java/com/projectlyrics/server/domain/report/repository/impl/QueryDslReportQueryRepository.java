package com.projectlyrics.server.domain.report.repository.impl;

import static com.projectlyrics.server.domain.report.domain.QReport.report;
import com.projectlyrics.server.domain.report.domain.Report;
import com.projectlyrics.server.domain.report.repository.ReportQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslReportQueryRepository implements ReportQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Report> findById(Long reportId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(report)
                        .leftJoin(report.reporter).fetchJoin()
                        .leftJoin(report.note).fetchJoin()
                        .leftJoin(report.comment).fetchJoin()
                        .where(
                                report.id.eq(reportId),
                                report.deletedAt.isNull()
                        )
                        .fetchOne());
    }

    @Override
    public Optional<Report> findByReporterIdAndNoteIdAndCommentId(Long reporterId, Long noteId, Long commentId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(report)
                        .leftJoin(report.reporter).fetchJoin()
                        .leftJoin(report.note).fetchJoin()
                        .leftJoin(report.comment).fetchJoin()
                        .where(
                                report.reporter.id.eq(reporterId),
                                commentId != null ? report.comment.id.eq(commentId) : report.comment.id.isNull(),
                                noteId != null ? report.note.id.eq(noteId) : report.note.id.isNull(),
                                report.deletedAt.isNull()
                        )
                        .fetchOne());
    }
}
