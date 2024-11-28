package com.projectlyrics.server.global.slack.domain;

public enum SlackAction {

    REPORT_ACCEPT,
    REPORT_FAKE,
    DISCIPLINE,
    ;

    public static SlackAction from(String actionId) {
        if (actionId.startsWith("discipline")) {
            return DISCIPLINE;
        }

        if (actionId.startsWith("report")) {
            if (actionId.contains("accept")) {
                return REPORT_ACCEPT;
            }

            if (actionId.contains("fake")) {
                return REPORT_FAKE;
            }
        }

        return null;
    }

    public boolean isReport() {
        return this == REPORT_FAKE || this == REPORT_ACCEPT;
    }

    public boolean isDiscipline() {
        return this == DISCIPLINE;
    }
}
