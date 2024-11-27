package com.projectlyrics.server.global.slack.api;

import static com.projectlyrics.server.global.slack.domain.SlackAction.DISCIPLINE;
import static com.projectlyrics.server.global.slack.domain.SlackAction.REPORT_ACCEPT;
import static com.projectlyrics.server.global.slack.domain.SlackAction.REPORT_FAKE;

import com.projectlyrics.server.domain.comment.exception.InvalidCommentDeletionException;
import com.projectlyrics.server.domain.discipline.exception.InvalidDisciplineCreateException;
import com.projectlyrics.server.domain.note.exception.InvalidNoteDeletionException;
import com.projectlyrics.server.domain.report.exception.ReportNotFoundException;
import com.projectlyrics.server.global.slack.domain.Slack;
import com.projectlyrics.server.global.slack.domain.SlackAction;
import com.projectlyrics.server.global.slack.exception.SlackFeedbackFailureException;
import com.projectlyrics.server.global.slack.exception.SlackInteractionFailureException;
import com.projectlyrics.server.global.slack.service.SlackService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/api/v1/slack/interactive")
@RequiredArgsConstructor
public class SlackController {

    @Value("${slack.token}")
    private String token;

    @Value("${slack.channel.id}")
    private String channelId;

    private final SlackService slackService;
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping
    public ResponseEntity<Void> handleInteractiveMessage(HttpServletRequest request) {
        try {
            Slack slack = new Slack(request);
            SlackAction actionId = slack.getActionId();
            JSONArray blocks = new JSONArray();

            if (actionId.equals(REPORT_ACCEPT) || actionId.equals(REPORT_FAKE)) {
                blocks = slackService.resolveReport(slack);
            } else if (actionId.equals(DISCIPLINE)) {
                blocks = slackService.createDiscipline(slack);
            }

            sendFeedbackToSlack(blocks, slack.getThreadTimestamp());
            return ResponseEntity
                    .ok(null);
        } catch (ReportNotFoundException |
                 InvalidNoteDeletionException |
                 InvalidCommentDeletionException |
                 InvalidDisciplineCreateException e
        ) {
            throw e;
        } catch (JSONException e) {
            log.error("JSON parsing error: {}", e.getMessage());
            throw new SlackInteractionFailureException();
        } catch (Exception e) {
            log.error("Failed to send message to Slack", e);
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
            throw new SlackFeedbackFailureException();
        }
    }
}
