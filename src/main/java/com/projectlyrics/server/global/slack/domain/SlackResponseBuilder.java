package com.projectlyrics.server.global.slack.domain;

import java.time.LocalDate;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class SlackResponseBuilder {
    public static JSONObject createDivider() {
        return new JSONObject().put("type", "divider");
    }

    public static JSONObject createSection(String text) {
        return new JSONObject()
                .put("type", "section")
                .put("text", new JSONObject()
                        .put("type", "mrkdwn")
                        .put("text", text));
    }

    public static JSONObject createPlainTextInput(String actionId, String label, String placeholder, String blockId) {
        return new JSONObject()
                .put("type", "input")
                .put("block_id", blockId)
                .put("element", new JSONObject()
                        .put("type", "plain_text_input")
                        .put("action_id", actionId)
                        .put("placeholder", createPlainText(placeholder)))
                .put("label", createPlainText(label));
    }

    public static <E extends Enum<E> & SlackSelectEnum> JSONObject createMultiStaticSelect(
            String actionId, String label, List<E> SelectList , String blockId, String description) {

        JSONArray options = new JSONArray();
        for (E select : SelectList) {
            options.put(new JSONObject()
                    .put("text", createPlainText(select.getDescription()))
                    .put("value", select.name()));
        }

        return new JSONObject()
                .put("type", "input")
                .put("block_id", blockId)
                .put("element", new JSONObject()
                        .put("type", "multi_static_select")
                        .put("placeholder", createPlainText(description))
                        .put("options", options)
                        .put("action_id", actionId))
                .put("label", createPlainText(label));
    }


    public static JSONObject createDatepicker(String actionId, String label, String blockId, LocalDate initialDate) {
        return new JSONObject()
                .put("type", "input")
                .put("block_id", blockId)
                .put("element", new JSONObject()
                        .put("type", "datepicker")
                        .put("initial_date", initialDate.toString())
                        .put("placeholder", createPlainText("날짜를 선택하세요"))
                        .put("action_id", actionId))
                .put("label", createPlainText(label));
    }

    public static JSONObject createButton(String text, String actionId, JSONObject value) {
        return new JSONObject()
                .put("type", "button")
                .put("text", createPlainText(text))
                .put("action_id", actionId)
                .put("value", value.toString());
    }

    public static JSONObject createSubmitSection(Slack slack, String description) {
        JSONObject value = new JSONObject()
                .put("userId", slack.userId)
                .put("reportId", slack.reportId)
                .put("artistId", slack.artistId);

        return new JSONObject()
                .put("type", "section")
                .put("text", createMarkdownText(description))
                .put("accessory", createButton("제출", "discipline_submit", value));
    }

    private static JSONObject createPlainText(String text) {
        return new JSONObject().put("type", "plain_text").put("text", text).put("emoji", true);
    }

    private static JSONObject createMarkdownText(String text) {
        return new JSONObject().put("type", "mrkdwn").put("text", text);
    }
}
