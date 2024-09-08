package com.projectlyrics.server.domain.report.service;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.exception.CommentNotFoundException;
import com.projectlyrics.server.domain.comment.repository.CommentQueryRepository;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.exception.NoteNotFoundException;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.projectlyrics.server.domain.report.domain.Report;
import com.projectlyrics.server.domain.report.domain.ReportCreate;
import com.projectlyrics.server.domain.report.domain.ReportResolve;
import com.projectlyrics.server.domain.report.dto.request.ReportCreateRequest;
import com.projectlyrics.server.domain.report.dto.request.ReportResolveRequest;
import com.projectlyrics.server.domain.report.exception.ReportNotFoundException;
import com.projectlyrics.server.domain.report.repository.ReportCommandRepository;
import com.projectlyrics.server.domain.report.repository.ReportQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.global.slack.SlackClient;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportCommandService {

    private final ReportCommandRepository reportCommandRepository;
    private final ReportQueryRepository reportQueryRepository;
    private final UserQueryRepository userQueryRepository;
    private final NoteQueryRepository noteQueryRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final SlackClient slackClient;

    public Report create(ReportCreateRequest request, Long reporterId) {

        User reporter = userQueryRepository.findById(reporterId)
                .orElseThrow(UserNotFoundException::new);
        Note note = Optional.ofNullable(request.noteId())
                .map(noteId -> noteQueryRepository.findById(noteId)
                        .orElseThrow(NoteNotFoundException::new))
                .orElse(null);
        Comment comment = Optional.ofNullable(request.commentId())
                .map(commentId -> commentQueryRepository.findById(commentId)
                        .orElseThrow(CommentNotFoundException::new))
                .orElse(null);

        Optional<Report> existingReport = reportQueryRepository.findByReporterIdAndNoteIdAndCommentId(
                reporterId, request.noteId(), request.commentId());

        Report report = existingReport
                .map(r -> {
                    r.setReportReason(request.reportReason());
                    return r;
                })
                .orElseGet(() -> {
                    return Report.create(ReportCreate.of(reporter, note, comment, request.reportReason(), request.email()));
                });

        Report savedReport =  reportCommandRepository.save(report);

        if (savedReport.getNote() != null) {
            slackClient.sendNoteReportMessage(savedReport);
        } else if (savedReport.getComment() != null) {
            slackClient.sendCommentReportMessage(savedReport);
        }

        return savedReport;
    }

    public Long resolve(ReportResolveRequest reportResolveRequest, Long reportId) {

        Report report = reportQueryRepository.findById(reportId)
                .orElseThrow(ReportNotFoundException::new);
        report.resolve(ReportResolve.of(reportResolveRequest.approvalStatus(), reportResolveRequest.isFalseReport()));

        return report.getId();
    }
}
