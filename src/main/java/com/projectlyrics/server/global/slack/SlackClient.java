package com.projectlyrics.server.global.slack;

import com.projectlyrics.server.domain.report.domain.Report;
import com.projectlyrics.server.global.slack.exception.SlackNotificationFailureException;
import com.projectlyrics.server.global.slack.exception.SlackNotificationProcessingException;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.element.ButtonElement;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlackClient {

    @Value("${slack.token}")
    private String token;

    @Value("${slack.channel.id}")
    private String channelId;

    private static final Logger logger = LoggerFactory.getLogger(SlackClient.class);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void sendMessage(List<LayoutBlock> blocks, String message) {
        try {
            Slack slack = Slack.getInstance();
            ChatPostMessageResponse chatPostMessageResponse = slack.methods(token).chatPostMessage(req -> req
                    .channel(channelId)
                    .blocks(blocks)
                    .text("Report Notification")
            );

            if (!chatPostMessageResponse.isOk()) {
                logger.error("Slack API error: {}", chatPostMessageResponse.getError());
                throw new SlackNotificationFailureException();
            }
        } catch (IOException | SlackApiException e) {
            logger.error("Failed to send Slack message: {}", e.getMessage(), e);
            throw new SlackNotificationProcessingException();
        }
    }

    public void sendNoteReportMessage(Report report) {
        sendReportMessage(
                report,
                "노트",
                report.getNote().getPublisher().getId(),
                report.getNote().getContent()
        );
    }

    public void sendCommentReportMessage(Report report) {
        sendReportMessage(
                report,
                "댓글",
                report.getComment().getWriter().getId(),
                report.getComment().getContent()
        );
    }

    private void sendReportMessage(Report report, String contentType, Long contentId, String content) {
        List<LayoutBlock> blocks = List.of(
                Blocks.section(section -> section.text(
                        MarkdownTextObject.builder().text(":rotating_light: *" + report.getId() + ") 새로운 신고가 접수되었습니다.*").build())),
                Blocks.section(section -> section.fields(List.of(
                        MarkdownTextObject.builder().text("*신고자 ID:* " + report.getReporter().getId()).build(),
                        MarkdownTextObject.builder().text("*피신고자 ID:* " + contentId).build(),
                        MarkdownTextObject.builder().text("*" + contentType + " ID:* " + contentId).build(),
                        MarkdownTextObject.builder().text("*신고 이유:* " + report.getReportReason().getDescription()).build(),
                        MarkdownTextObject.builder().text("*신고자 이메일:* " + (report.getEmail() != null ? report.getEmail() : "-")).build(),
                        MarkdownTextObject.builder().text("*신고 일시:* " + formatter.format(report.getCreatedAt())).build()
                ))),
                Blocks.section(section -> section.text(MarkdownTextObject.builder().text("*" + contentType + " 내용:*\n" + (content != null ? content : "-")).build())),
                Blocks.actions(actions -> actions.elements(List.of(
                        ButtonElement.builder()
                                .text(PlainTextObject.builder().text("Accept").build())
                                .actionId("report_accept")
                                .style("primary")
                                .value("{\"type\":\"accepted\", \"reportId\":\"" + report.getId() + "\", \"approvalStatus\":\"ACCEPTED\", \"isFalseReport\":false}")
                                .build(),
                        ButtonElement.builder()
                                .text(PlainTextObject.builder().text("Dismiss").build())
                                .actionId("report_dismiss")
                                .style("danger")
                                .value("{\"type\":\"dismissed\", \"reportId\":\"" + report.getId() + "\", \"approvalStatus\":\"DISMISSED\", \"isFalseReport\":false}")
                                .build(),
                        ButtonElement.builder()
                                .text(PlainTextObject.builder().text("Fake Report").build())
                                .actionId("report_fake")
                                .value("{\"type\":\"fake report\", \"reportId\":\"" + report.getId() + "\", \"approvalStatus\":\"DISMISSED\", \"isFalseReport\":true}")
                                .build()
                )))
        );

        sendMessage(blocks, contentType + " Report Notification");
    }
}
