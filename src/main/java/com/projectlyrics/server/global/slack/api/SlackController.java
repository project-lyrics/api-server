package com.projectlyrics.server.global.slack.api;

import static com.projectlyrics.server.domain.discipline.domain.DisciplineReason.FAKE_REPORT;
import static com.projectlyrics.server.global.slack.domain.SlackAction.DISCIPLINE;
import static com.projectlyrics.server.global.slack.domain.SlackAction.REPORT_ACCEPT;
import static com.projectlyrics.server.global.slack.domain.SlackAction.REPORT_FAKE;
import static com.projectlyrics.server.global.slack.domain.SlackResponseBuilder.createDatepicker;
import static com.projectlyrics.server.global.slack.domain.SlackResponseBuilder.createDivider;
import static com.projectlyrics.server.global.slack.domain.SlackResponseBuilder.createMultiStaticSelect;
import static com.projectlyrics.server.global.slack.domain.SlackResponseBuilder.createPlainTextInput;
import static com.projectlyrics.server.global.slack.domain.SlackResponseBuilder.createSection;
import static com.projectlyrics.server.global.slack.domain.SlackResponseBuilder.createSubmitSection;

import com.projectlyrics.server.domain.comment.exception.InvalidCommentDeletionException;
import com.projectlyrics.server.domain.discipline.domain.Discipline;
import com.projectlyrics.server.domain.discipline.domain.DisciplineReason;
import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import com.projectlyrics.server.domain.discipline.dto.request.DisciplineCreateRequest;
import com.projectlyrics.server.domain.discipline.exception.InvalidDisciplineCreateException;
import com.projectlyrics.server.domain.discipline.service.DisciplineCommandService;
import com.projectlyrics.server.domain.note.exception.InvalidNoteDeletionException;
import com.projectlyrics.server.domain.report.dto.request.ReportResolveRequest;
import com.projectlyrics.server.domain.report.exception.ReportNotFoundException;
import com.projectlyrics.server.domain.report.service.ReportCommandService;
import com.projectlyrics.server.global.slack.domain.Slack;
import com.projectlyrics.server.global.slack.domain.SlackAction;
import com.projectlyrics.server.global.slack.domain.SlackSelectEnum;
import com.projectlyrics.server.global.slack.exception.SlackFeedbackFailureException;
import com.projectlyrics.server.global.slack.exception.SlackInteractionFailureException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@RestController
@RequestMapping("/api/v1/slack/interactive")
@RequiredArgsConstructor
public class SlackController {

    @Value("${slack.token}")
    private String token;

    @Value("${slack.channel.id}")
    private String channelId;

    private final ReportCommandService reportCommandService;
    private final DisciplineCommandService disciplineCommandService;
    private final RestTemplate restTemplate = new RestTemplate(); // HTTP 요청을 보내기 위한 RestTemplate
    private static final Logger logger = LoggerFactory.getLogger(SlackController.class);

    @PostMapping
    public ResponseEntity<Void> handleInteractiveMessage(HttpServletRequest request) {
        try {
            Slack slack = new Slack(request);

            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
            String payload = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
            // Slack에서 보낸 payload 디코딩 및 JSON 변환
            String decodedPayload = URLDecoder.decode(payload, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(decodedPayload.substring("payload=".length()));

            // 액션 정보 추출
            SlackAction actionId = slack.getActionId();

            // 메시지 블록 생성
            JSONArray blocks = new JSONArray();

            if (actionId.equals(REPORT_ACCEPT) || actionId.equals(REPORT_FAKE)) {
                reportCommandService.resolve(ReportResolveRequest.of(slack.getApprovalStatus(), slack.getIsFalseReport()), slack.getReportId());

                blocks.put(createSection(
                        ":white_check_mark: *승인 여부*: " + slack.getApprovalStatus() +
                                "  *허위 신고 여부*: " + slack.getIsFalseReport()
                ));

                if (actionId.equals(REPORT_ACCEPT)) {
                    addDiscipline(slack.getUserId(), slack.getReportId(), slack.getArtistId(), blocks, DisciplineReason.getOtherTypes());
                }
                else {
                    addDiscipline(slack.getUserId(), slack.getReportId(), slack.getArtistId(), blocks, DisciplineReason.getFakeReportType());
                }
            }

            else if (actionId.equals(DISCIPLINE)) {
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
            }

            sendFeedbackToSlack(blocks, slack.getThreadTimestamp());

            return ResponseEntity.ok().build();
        } catch (ReportNotFoundException |
                 InvalidNoteDeletionException |
                 InvalidCommentDeletionException |
                 InvalidDisciplineCreateException e
        ) {
            throw e;
        } catch (JSONException e) {
            System.out.println("JSON Parsing Error: " + e.getMessage());
            throw new SlackInteractionFailureException();
        }
        catch (Exception e) {
            logger.error("Failed to send message to Slack", e);
            System.out.println(e);
            throw new SlackInteractionFailureException();
        }
    }

    private void sendFeedbackToSlack(JSONArray blocks, String threadTs) {
        try {
            // 최상위 JSONObject 생성
            JSONObject responseJson = new JSONObject();
            responseJson.put("channel", channelId);  // 슬랙 채널 ID 설정
            responseJson.put("blocks", blocks);  // blocks는 JSONArray로 전달

            // 스레드에 답장할 경우 thread_ts 포함
            if (threadTs != null && !threadTs.isEmpty()) {
                responseJson.put("thread_ts", threadTs);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);  // 슬랙 봇 토큰

            HttpEntity<String> entity = new HttpEntity<>(responseJson.toString(), headers);
            String slackApiUrl = "https://slack.com/api/chat.postMessage";

            restTemplate.postForEntity(slackApiUrl, entity, String.class);
        } catch (Exception e) {
            System.out.println(e);
            throw new SlackFeedbackFailureException();
        }
    }

    private <E extends Enum<E> & SlackSelectEnum> void addDiscipline(Long userId, Long reportId, Long artistId, JSONArray blocks, List<E> disciplineReason) {
        blocks.put(createDivider())
                .put(createSection(":pencil2: *조치 관련 설정*"))
                .put(createDatepicker("discipline_start", "조치 시작 날짜", "start", LocalDate.now()))
                .put(createMultiStaticSelect(
                        "discipline_type",
                        "사용자 " + userId + "에 대한 조치",
                        DisciplineType.getAllTypes(),
                        "type",
                        "옵션을 선택하세요"
                ))
                .put(createMultiStaticSelect(
                        "discipline_reason",
                        "사용자 " + userId + "에 대한 조치 이유",
                        disciplineReason,
                        "reason",
                        "이유를 선택하세요"
                ))
                .put(createPlainTextInput(
                        "discipline_content",
                        "사용자에 전달될 조치 알림 메시지",
                        "{시작시간}, {종료시간}을 포함해서 알림 메시지를 작성해보세요 (단 영구 탈퇴와 경고의 경우는 제외)",
                        "content"
                ))
                .put(createSubmitSection(userId, reportId, artistId, "사용자에 대한 조치 및 이유를 선택하고 제출해주세요."));
    }

}
