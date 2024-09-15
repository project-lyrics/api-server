package com.projectlyrics.server.global.slack.api;

import com.projectlyrics.server.domain.report.domain.ApprovalStatus;
import com.projectlyrics.server.domain.report.dto.request.ReportResolveRequest;
import com.projectlyrics.server.domain.report.service.ReportCommandService;
import com.projectlyrics.server.global.slack.exception.SlackFeedbackFailureException;
import com.projectlyrics.server.global.slack.exception.SlackInteractionFailureException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/slack/interactive")
@RequiredArgsConstructor
public class SlackController {

    private final ReportCommandService reportCommandService;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String SLACK_SIGNING_SECRET = "your-signing-secret"; // Slack에서 제공하는 서명 비밀 키

    @PostMapping
    public ResponseEntity<Void> handleInteractiveMessage(
            @RequestParam Map<String, String> paramMap,
            @RequestHeader("X-Slack-Signature") String slackSignature,
            @RequestHeader("X-Slack-Request-Timestamp") String slackTimestamp) {

        try {
            // 서명 검증 (Slack 요청 유효성 검증)
            if (!isRequestValid(paramMap, slackSignature, slackTimestamp)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Slack에서 보낸 payload 디코딩 및 JSON 변환
            String payload = paramMap.get("payload");
            JSONObject json = new JSONObject(payload);

            // 액션 정보 추출
            JSONObject action = json.getJSONArray("actions").getJSONObject(0);
            String actionId = action.getString("action_id");
            JSONObject valueJson = new JSONObject(action.getString("value"));

            //응답 메시지
            String message = "";

            // actionId에 따라 처리
            if (actionId.startsWith("report_")) {
                String type = valueJson.getString("type");
                Long reportId = valueJson.getLong("reportId");
                ApprovalStatus approvalStatus = ApprovalStatus.valueOf(valueJson.getString("approvalStatus"));
                Boolean isFalseReport = valueJson.getBoolean("isFalseReport");

                reportCommandService.resolve(ReportResolveRequest.of(approvalStatus, isFalseReport), reportId);
                message = ":white_check_mark: " + type + " pressed\n승인여부: " + approvalStatus + "   허위신고 여부: " + isFalseReport;
            }

            // Slack 응답 URL로 피드백 전송
            sendFeedbackToSlack(json.getString("response_url"), message);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SlackInteractionFailureException();
        }
    }

    private void sendFeedbackToSlack(String responseUrl, String message) {
        try {
            JSONObject responseJson = new JSONObject();
            responseJson.put("text", message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(responseJson.toString(), headers);

            restTemplate.postForEntity(responseUrl, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SlackFeedbackFailureException();
        }
    }

    // Slack 서명 검증 메소드
    private boolean isRequestValid(Map<String, String> paramMap, String slackSignature, String slackTimestamp) {
        try {
            String payload = paramMap.get("payload");
            String baseString = "v0:" + slackTimestamp + ":" + payload;

            SecretKeySpec signingKey = new SecretKeySpec(SLACK_SIGNING_SECRET.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(baseString.getBytes());
            String expectedSignature = "v0=" + bytesToHex(rawHmac);

            return expectedSignature.equals(slackSignature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
