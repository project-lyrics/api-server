package com.projectlyrics.server.global.slack.api;

import com.projectlyrics.server.domain.comment.exception.InvalidCommentDeletionException;
import com.projectlyrics.server.domain.discipline.exception.InvalidDisciplineCreateException;
import com.projectlyrics.server.domain.note.exception.InvalidNoteDeletionException;
import com.projectlyrics.server.domain.report.exception.ReportNotFoundException;
import com.projectlyrics.server.global.slack.domain.Slack;
import com.projectlyrics.server.global.slack.domain.SlackAction;
import com.projectlyrics.server.global.slack.exception.SlackInteractionFailureException;
import com.projectlyrics.server.global.slack.service.SlackService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/slack/interactive")
@RequiredArgsConstructor
public class SlackController {

    @Value("${slack.channel.id}")
    private String channelId;

    private final SlackService slackService;

    @PostMapping
    public ResponseEntity<Void> handleInteractiveMessage(HttpServletRequest request) {
        try {
            Slack slack = new Slack(request);
            SlackAction actionId = slack.getActionId();
            JSONArray blocks = new JSONArray();

            if (actionId.isReport()) {
                blocks = slackService.resolveReport(slack);
            } else if (actionId.isDiscipline()) {
                blocks = slackService.createDiscipline(slack);
            }

            slackService.sendFeedbackToSlack(blocks, slack.getThreadTimestamp(), channelId);
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
}
