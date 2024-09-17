package com.projectlyrics.server.global.slack.api;

import com.projectlyrics.server.domain.report.domain.ApprovalStatus;
import com.projectlyrics.server.domain.report.dto.request.ReportResolveRequest;
import com.projectlyrics.server.domain.report.service.ReportCommandService;
import com.projectlyrics.server.global.slack.exception.SlackFeedbackFailureException;
import com.projectlyrics.server.global.slack.exception.SlackInteractionFailureException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

            // 응답 메세지
            String message = "";
            String threadTs = json.optString("message_ts");  // Extract thread_ts if present

            // actionId에 따라 처리
            if (actionId.startsWith("report_")) {
                String type = valueJson.getString("type");
                Long reportId = valueJson.getLong("reportId");
                ApprovalStatus approvalStatus = ApprovalStatus.valueOf(valueJson.getString("approvalStatus"));
                Boolean isFalseReport = valueJson.getBoolean("isFalseReport");

                reportCommandService.resolve(ReportResolveRequest.of(approvalStatus, isFalseReport), reportId);
                message = ":white_check_mark: *" + type + " pressed)*\n승인여부 : " + approvalStatus + "   허위신고여부: " + isFalseReport;
            }

            sendFeedbackToSlack(message, threadTs);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SlackInteractionFailureException();
        }
    }

    private void sendFeedbackToSlack(String message, String threadTs) {
        try {
            JSONObject responseJson = new JSONObject();
            responseJson.put("channel", channelId);  // 채널 ID
            responseJson.put("text", message);

            // 스레드에 답장할 경우
            if (threadTs != null && !threadTs.isEmpty()) {
                responseJson.put("thread_ts", threadTs);  // 스레드의 타임스탬프
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);  // UTF-8 인코딩
            headers.set("Authorization", "Bearer " + token);  // Slack 봇 토큰 설정

            HttpEntity<String> entity = new HttpEntity<>(responseJson.toString(), headers);
            String slackApiUrl = "https://slack.com/api/chat.postMessage";  // Slack API URL

            ResponseEntity<String> response = restTemplate.postForEntity(slackApiUrl, entity, String.class);
            String responseBody = response.getBody();
            System.out.println("Slack API Response: " + responseBody);

            // 응답에서 오류 확인
            JSONObject jsonResponse = new JSONObject(responseBody);
            if (!jsonResponse.getBoolean("ok")) {
                throw new SlackFeedbackFailureException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SlackFeedbackFailureException();
        }
    }
}
