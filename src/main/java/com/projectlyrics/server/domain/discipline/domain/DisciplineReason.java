package com.projectlyrics.server.domain.discipline.domain;

public enum DisciplineReason {
    INAPPROPRIATE_CONTENT("커뮤니티 성격에 맞지 않음"),
    DEFAMATION("타 유저 혹은 아티스트 비방"),
    EXPLICIT_CONTENT("불쾌감을 조성하는 음란성/선정적인 내용"),
    COMMERCIAL_ADS("상업적 광고"),
    INFO_DISCLOSURE("부적절한 정보 유출"),
    POLITICAL_RELIGIOUS("정치적인 내용/종교 포교 시도"),
    OTHER("기타"),
    FAKE_REPORT("허위 신고")
    ;

    private final String description;

    DisciplineReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}