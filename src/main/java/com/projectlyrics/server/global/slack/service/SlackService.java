package com.projectlyrics.server.global.slack.service;

import static com.projectlyrics.server.domain.discipline.domain.DisciplineReason.FAKE_REPORT;
import static com.projectlyrics.server.global.slack.domain.SlackResponseBuilder.createDatepicker;
import static com.projectlyrics.server.global.slack.domain.SlackResponseBuilder.createDivider;
import static com.projectlyrics.server.global.slack.domain.SlackResponseBuilder.createMultiStaticSelect;
import static com.projectlyrics.server.global.slack.domain.SlackResponseBuilder.createPlainTextInput;
import static com.projectlyrics.server.global.slack.domain.SlackResponseBuilder.createSection;
import static com.projectlyrics.server.global.slack.domain.SlackResponseBuilder.createSubmitSection;

import com.projectlyrics.server.domain.discipline.domain.Discipline;
import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import com.projectlyrics.server.domain.discipline.dto.request.DisciplineCreateRequest;
import com.projectlyrics.server.domain.discipline.service.DisciplineCommandService;
import com.projectlyrics.server.domain.report.dto.request.ReportResolveRequest;
import com.projectlyrics.server.domain.report.service.ReportCommandService;
import com.projectlyrics.server.global.slack.domain.Slack;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SlackService {

    private final ReportCommandService reportCommandService;
    private final DisciplineCommandService disciplineCommandService;

    public JSONArray resolveReport(Slack slack) {
        JSONArray blocks = new JSONArray();

        reportCommandService.resolve(ReportResolveRequest.of(slack.getApprovalStatus(), slack.getIsFalseReport()), slack.getReportId());
        blocks.put(createSection(
                ":white_check_mark: *승인 여부*: " + slack.getApprovalStatus() +
                        "  *허위 신고 여부*: " + slack.getIsFalseReport()
        ));

        addDiscipline(slack, blocks);
        return blocks;
    }

    private void addDiscipline(Slack slack, JSONArray blocks) {
        blocks.put(createDivider())
                .put(createSection(":pencil2: *조치 관련 설정*"))
                .put(createDatepicker("discipline_start", "조치 시작 날짜", "start", LocalDate.now()))
                .put(createMultiStaticSelect(
                        "discipline_type",
                        "사용자 " + slack.getUserId() + "에 대한 조치",
                        DisciplineType.getAllTypes(),
                        "type",
                        "옵션을 선택하세요"
                ))
                .put(createMultiStaticSelect(
                        "discipline_reason",
                        "사용자 " + slack.getUserId() + "에 대한 조치 이유",
                        slack.getActionId().getReasons(),
                        "reason",
                        "이유를 선택하세요"
                ))
                .put(createPlainTextInput(
                        "discipline_content",
                        "사용자에 전달될 조치 알림 메시지",
                        "{시작시간}, {종료시간}을 포함해서 알림 메시지를 작성해보세요 (단 영구 탈퇴와 경고의 경우는 제외)",
                        "content"
                ))
                .put(createSubmitSection(slack, "사용자에 대한 조치 및 이유를 선택하고 제출해주세요."));
    }

    public JSONArray createDiscipline(Slack slack) {
        JSONArray blocks = new JSONArray();
        Discipline discipline = disciplineCommandService.create(DisciplineCreateRequest.of(slack.getUserId(), slack.getArtistId(), slack.getDisciplineReason(), slack.getDisciplineType(), slack.getStart(), slack.getDisciplineContent()));

        // 조치가 들어오면 (허위 신고가 아닌 건에 한해) 해당 노트/댓글 삭제
        if (slack.getDisciplineReason() != FAKE_REPORT) {
            reportCommandService.deleteReportedTarget(slack.getReportId());
        }

        blocks.put(createSection(
                ":mega: *사용자 " + slack.getUserId() + "에 대한 조치가 완료되었습니다.*" +
                        "\n*조치 사유:* " + slack.getDisciplineReason().getDescription() +
                        "\n*조치 내용:* " + slack.getDisciplineType().getDescription() +
                        "\n*조치 기간:* " + discipline.getStartTime() + " ~ " + discipline.getEndTime()
        ));

        return blocks;
    }
}
