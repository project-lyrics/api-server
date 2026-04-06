package com.projectlyrics.server.domain.note.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NoteTypeCaseInsensitiveTest {

    @Test
    void 소문자_문자열도_노트타입으로_변환한다() {
        NoteType result = NoteType.of("free");

        assertThat(result).isEqualTo(NoteType.FREE);
    }

    @Test
    void 혼합_대소문자_문자열도_노트타입으로_변환한다() {
        NoteType result = NoteType.of("lyrics_analysis");

        assertThat(result).isEqualTo(NoteType.LYRICS_ANALYSIS);
    }
}
