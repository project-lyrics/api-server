package com.projectlyrics.server.global.slack.domain;

import com.projectlyrics.server.domain.discipline.domain.DisciplineReason;

import java.util.List;

public enum SlackAction {

    REPORT_ACCEPT,
    REPORT_FAKE,
    DISCIPLINE,
    ;

    public boolean isReport() {
        return this.equals(REPORT_FAKE) || this.equals(REPORT_ACCEPT);
    }

    public boolean isDiscipline() {
        return this.equals(DISCIPLINE);
    }

    public List<DisciplineReason> getReasons() {
        if (this.equals(REPORT_ACCEPT)) {
            return DisciplineReason.getOtherTypes();
        }

        if (this.equals(REPORT_FAKE)) {
            return DisciplineReason.getFakeReportType();
        }

        return null;
    }
}
