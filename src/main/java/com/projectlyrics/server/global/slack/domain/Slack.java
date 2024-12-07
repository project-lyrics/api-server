package com.projectlyrics.server.global.slack.domain;

import com.projectlyrics.server.domain.discipline.domain.DisciplineReason;
import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import com.projectlyrics.server.domain.discipline.exception.InvalidDisciplineCreateException;
import com.projectlyrics.server.domain.report.domain.ApprovalStatus;
import com.projectlyrics.server.global.slack.exception.SlackInteractionFailureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.projectlyrics.server.global.slack.domain.SlackAction.*;

@Getter
public class Slack {

    SlackAction actionId;
    Long reportId;
    String threadTimestamp;
    Long userId;
    Long artistId;

    ApprovalStatus approvalStatus;
    Boolean isFalseReport;

    LocalDateTime start;
    DisciplineType disciplineType;
    DisciplineReason disciplineReason;
    String disciplineContent;

    public Slack(HttpServletRequest request) {
        JSONObject json = getJsonFrom(request);

        JSONObject action = json.getJSONArray("actions").getJSONObject(0);
        JSONObject value = new JSONObject(action.getString("value"));

        actionId = SlackAction.valueOf(action.getString("action_id").toUpperCase());
        reportId = value.getLong("reportId");
        threadTimestamp = json.getJSONObject("container").optString("message_ts");
        userId = value.getLong("userId");
        artistId = value.getLong("artistId");

        if (actionId.equals(REPORT_ACCEPT) || actionId.equals(REPORT_FAKE)) {
            approvalStatus = ApprovalStatus.valueOf(value.getString("approvalStatus"));
            isFalseReport = value.getBoolean("isFalseReport");
        }

        if (actionId.equals(DISCIPLINE)) {
            String startDateString = json.getJSONObject("state").getJSONObject("values")
                    .getJSONObject("start").getJSONObject("discipline_start").getString("selected_date");
            JSONArray selectedDisciplineType = json.getJSONObject("state").getJSONObject("values")
                    .getJSONObject("type").getJSONObject("discipline_type").getJSONArray("selected_options");
            JSONArray selectedDisciplineReason = json.getJSONObject("state").getJSONObject("values")
                    .getJSONObject("reason").getJSONObject("discipline_reason").getJSONArray("selected_options");

            start = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            disciplineType = DisciplineType.valueOf(selectedDisciplineType.getJSONObject(0).getString("value"));
            disciplineReason = DisciplineReason.valueOf(selectedDisciplineReason.getJSONObject(0).getString("value"));
            disciplineContent = json.getJSONObject("state").getJSONObject("values")
                    .getJSONObject("content").getJSONObject("discipline_content").getString("value");

            if (Objects.isNull(disciplineContent) || disciplineContent.isEmpty()) {
                throw new InvalidDisciplineCreateException();
            }
        }
    }
    
    private JSONObject getJsonFrom(HttpServletRequest request) {
        try {
            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
            String payload = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
            String decodedPayload = URLDecoder.decode(payload, StandardCharsets.UTF_8);

            return new JSONObject(decodedPayload.substring("payload=".length()));
        } catch (UnsupportedEncodingException e) {
            throw new SlackInteractionFailureException();
        }
    }
}
