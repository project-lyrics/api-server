package com.projectlyrics.server.global.slack.api;

import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import com.projectlyrics.server.domain.report.domain.ApprovalStatus;
import com.projectlyrics.server.domain.report.domain.ReportReason;
import com.projectlyrics.server.domain.report.dto.request.ReportResolveRequest;
import com.projectlyrics.server.domain.report.service.ReportCommandService;
import com.projectlyrics.server.global.slack.exception.SlackFeedbackFailureException;
import com.projectlyrics.server.global.slack.exception.SlackInteractionFailureException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
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


@RestController
@RequestMapping("/api/v1/slack/interactive")
@RequiredArgsConstructor
public class SlackController {

    @Value("${slack.token}")
    private String token;

    @Value("${slack.channel.id}")
    private String channelId;

    private final ReportCommandService reportCommandService;
    private final RestTemplate restTemplate = new RestTemplate(); // HTTP 요청을 보내기 위한 RestTemplate
    private static final Logger logger = LoggerFactory.getLogger(SlackController.class);


    @PostMapping
    public ResponseEntity<Void> handleInteractiveMessage(HttpServletRequest request) {
        try {
            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
            String payload = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
            // Slack에서 보낸 payload 디코딩 및 JSON 변환
            String decodedPayload = URLDecoder.decode(payload, StandardCharsets.UTF_8.name());
            JSONObject json = new JSONObject(decodedPayload.substring("payload=".length()));

            // 액션 정보 추출
            JSONObject action = json.getJSONArray("actions").getJSONObject(0);
            String actionId = action.getString("action_id");
            JSONObject valueJson = new JSONObject(action.getString("value"));

            // 스레드 타임스탬프 추출
            String threadTs = json.getJSONObject("container").optString("message_ts");  // Extract thread_ts if present

            // 메시지 블록 생성
            JSONArray blocks = new JSONArray();
            // actionId에 따라 처리
            if (actionId.startsWith("report_")) {
                Long reportId = valueJson.getLong("reportId");
                ApprovalStatus approvalStatus = ApprovalStatus.valueOf(valueJson.getString("approvalStatus"));
                Boolean isFalseReport = valueJson.getBoolean("isFalseReport");

                reportCommandService.resolve(ReportResolveRequest.of(approvalStatus, isFalseReport), reportId);
                // 메시지 블록에 추가
                blocks.put(new JSONObject()
                        .put("type", "section")
                        .put("text", new JSONObject()
                                .put("type", "mrkdwn")
                                .put("text", ":white_check_mark: *승인 여부*: " + approvalStatus + "\n*허위 신고 여부*: " + isFalseReport)
                        )
                );

                if (actionId.contains("accept") || actionId.contains("fake")) {
                    Long userId = valueJson.getLong("user");
                    addBlockOfDiscipline(userId, blocks);
                }
            }

            sendFeedbackToSlack(blocks, threadTs);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to send message to Slack", e);
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
            logger.error("Failed to send message to Slack", e);
            throw new SlackFeedbackFailureException();
        }
    }

    private void addBlockOfDiscipline(Long userId, JSONArray blocks) {
        // 조치 선택 폼을 blocks에 추가
        blocks.put(new JSONObject()
                .put("type", "input")
                .put("element", new JSONObject()
                        .put("type", "multi_static_select")
                        .put("placeholder", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", "옵션을 선택하세요")
                                .put("emoji", true)
                        )
                        .put("options", new JSONArray()
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.WARNING.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.WARNING.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ARTIST_3DAYS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ARTIST_3DAYS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ARTIST_14DAYS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ARTIST_14DAYS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ARTIST_30DAYS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ARTIST_30DAYS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ARTIST_3MONTHS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ARTIST_3MONTHS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ALL_3DAYS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ALL_3DAYS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ALL_14DAYS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ALL_14DAYS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ALL_30DAYS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ALL_30DAYS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ALL_3MONTHS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ALL_3MONTHS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.FORCED_WITHDRAWAL.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.FORCED_WITHDRAWAL.name())
                                )
                        )
                        .put("action_id", "multi_static_select_action")
                )
                .put("label", new JSONObject()
                        .put("type", "plain_text")
                        .put("text", ":pencil2: 사용자 " + userId + "에 대한 조치")
                        .put("emoji", true)
                )
        );


        // 징계 이유 선택 폼 추가
        blocks.put(new JSONObject()
                .put("type", "input")
                .put("element", new JSONObject()
                        .put("type", "multi_static_select")
                        .put("placeholder", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", "이유를 선택하세요")
                                .put("emoji", true)
                        )
                        .put("options", new JSONArray()
                                // ReportReason enum을 기반으로 옵션 생성
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", ReportReason.INAPPROPRIATE_CONTENT.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", ReportReason.INAPPROPRIATE_CONTENT.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", ReportReason.DEFAMATION.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", ReportReason.DEFAMATION.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", ReportReason.EXPLICIT_CONTENT.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", ReportReason.EXPLICIT_CONTENT.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", ReportReason.COMMERCIAL_ADS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", ReportReason.COMMERCIAL_ADS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", ReportReason.INFO_DISCLOSURE.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", ReportReason.INFO_DISCLOSURE.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", ReportReason.POLITICAL_RELIGIOUS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", ReportReason.POLITICAL_RELIGIOUS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", ReportReason.OTHER.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", ReportReason.OTHER.name())
                                )
                        )
                        .put("action_id", "multi_static_select_reason_action")
                )
                .put("label", new JSONObject()
                        .put("type", "plain_text")
                        .put("text", ":pencil2: 사용자 " + userId + "에 대한 조치 이유")
                        .put("emoji", true)
                )
        );

        // 제출 버튼 추가
        blocks.put(new JSONObject()
                .put("type", "section")
                .put("text", new JSONObject()
                        .put("type", "mrkdwn")
                        .put("text", "사용자에 대한 조치 및 이유를 선택하고 제출해주세요.")
                )
                .put("accessory", new JSONObject()
                        .put("type", "button")
                        .put("text", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", "제출")
                                .put("emoji", true)
                        )
                        .put("value", "submit_action")
                        .put("action_id", "button_action")
                )
        );
    }

}
