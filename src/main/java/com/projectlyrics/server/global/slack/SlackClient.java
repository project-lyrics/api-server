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
        // Build the blocks with buttons
        List<LayoutBlock> blocks = List.of(
                Blocks.section(section -> section.text(
                        MarkdownTextObject.builder().text(":rotating_light:*새로운 신고가 접수되었습니다.*").build())),
                Blocks.section(section -> section.fields(List.of(
                        MarkdownTextObject.builder().text("*신고자 ID:*\n" + report.getReporter().getId()).build(),
                        MarkdownTextObject.builder().text("*피신고자 ID:*\n" + report.getNote().getPublisher().getId()).build(),
                        MarkdownTextObject.builder().text("*노트 ID:*\n" + report.getNote().getId()).build(),
                        MarkdownTextObject.builder().text("*신고 이유:*\n" + report.getReportReason().getDescription()).build(),
                        MarkdownTextObject.builder().text("*신고자 이메일:*\n" + (report.getEmail() != null ? report.getEmail() : "-")).build(),
                        MarkdownTextObject.builder().text("*신고 일시:*\n" + report.getCreatedAt()).build()
                ))),
                Blocks.section(section -> section.text(MarkdownTextObject.builder().text("*노트 내용:*\n" + report.getNote().getContent()).build())),
                Blocks.actions(actions -> actions.elements(List.of(
                        ButtonElement.builder()
                                .text(PlainTextObject.builder().text("Accept").build())
                                .actionId("accept_report")
                                .style("primary")
                                .build(),
                        ButtonElement.builder()
                                .text(PlainTextObject.builder().text("Dismiss").build())
                                .actionId("dismiss_report")
                                .style("danger")
                                .build(),
                        ButtonElement.builder()
                                .text(PlainTextObject.builder().text("Fake Report").build())
                                .actionId("fake_report")
                                .build()
                )))
        );

        sendMessage(blocks, "Report Notification");
    }

    public void sendCommentReportMessage(Report report) {
        // Build the blocks with buttons
        List<LayoutBlock> blocks = List.of(
                Blocks.section(section -> section.text(MarkdownTextObject.builder().text(":rotating_light:*새로운 신고가 접수되었습니다.*").build())),
                Blocks.section(section -> section.fields(List.of(
                        MarkdownTextObject.builder().text("*신고자 ID:*\n" + report.getReporter().getId()).build(),
                        MarkdownTextObject.builder().text("*피신고자 ID:*\n" + report.getComment().getWriter().getId()).build(),
                        MarkdownTextObject.builder().text("*댓글 ID:*\n" + report.getComment().getId()).build(),
                        MarkdownTextObject.builder().text("*신고 이유:*\n" + report.getReportReason().getDescription()).build(),
                        MarkdownTextObject.builder().text("*신고자 이메일:*\n" + (report.getEmail() != null ? report.getEmail() : "-")).build(),
                        MarkdownTextObject.builder().text("*신고 일시:*\n" + report.getCreatedAt()).build()
                ))),
                Blocks.section(section -> section.text(MarkdownTextObject.builder().text("*댓글 내용:*\n" + report.getComment().getContent()).build())),
                Blocks.actions(actions -> actions.elements(List.of(
                        ButtonElement.builder()
                                .text(PlainTextObject.builder().text("Accept").build())
                                .actionId("accept_report")
                                .style("primary")
                                .build(),
                        ButtonElement.builder()
                                .text(PlainTextObject.builder().text("Dismiss").build())
                                .actionId("dismiss_report")
                                .style("danger")
                                .build(),
                        ButtonElement.builder()
                                .text(PlainTextObject.builder().text("Fake Report").build())
                                .actionId("fake_report")
                                .build()
                )))
        );

        sendMessage(blocks, "Report Notification");
    }

}

