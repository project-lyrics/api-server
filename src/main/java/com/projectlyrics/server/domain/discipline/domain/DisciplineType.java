package com.projectlyrics.server.domain.discipline.domain;

import java.time.Period;

public enum DisciplineType {
    WARNING("경고", Period.ZERO),
    ALL_3DAYS("3일간 모든 아티스트 레코드 노트/댓글 작성 제한", Period.ofDays(3)),
    ALL_14DAYS("14일간 모든 아티스트 레코드 노트/댓글 작성 제한", Period.ofDays(14)),
    ALL_30DAYS("30일간 모든 아티스트 레코드 노트/댓글 작성 제한", Period.ofDays(30)),
    ALL_3MONTHS("3개월간 모든 아티스트 레코드 노트/댓글 작성 제한", Period.ofMonths(3)),
    ARTIST_3DAYS("3일간 해당 아티스트 레코드 글 작성 제한", Period.ofDays(3)),
    ARTIST_14DAYS("14일간 해당 아티스트 레코드 노트/댓글 작성 제한", Period.ofDays(14)),
    ARTIST_30DAYS("30일간 해당 아티스트 레코드 노트/댓글 작성 제한", Period.ofDays(30)),
    ARTIST_3MONTHS("3개월간 해당 아티스트 레코드 노트/댓글 작성 제한", Period.ofMonths(3)),
    FORCED_WITHDRAWAL("강제 탈퇴(영구)", Period.ofYears(1000));

    private final String description;
    private final Period period;

    DisciplineType(String description, Period period) {
        this.description = description;
        this.period = period;
    }

    public String getDescription() {
        return description;
    }

    public Period getPeriod() {
        return period;
    }
}
