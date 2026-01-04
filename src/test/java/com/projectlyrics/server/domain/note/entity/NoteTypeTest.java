package com.projectlyrics.server.domain.note.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NoteTypeTest {

    @Test
    void FREE_타입을_문자열로부터_생성해야_한다() {
        // when
        NoteType result = NoteType.of("FREE");

        // then
        assertThat(result).isEqualTo(NoteType.FREE);
    }

    @Test
    void QUESTION_타입을_문자열로부터_생성해야_한다() {
        // when
        NoteType result = NoteType.of("QUESTION");

        // then
        assertThat(result).isEqualTo(NoteType.QUESTION);
    }

    @Test
    void LYRICS_ANALYSIS_타입을_문자열로부터_생성해야_한다() {
        // when
        NoteType result = NoteType.of("LYRICS_ANALYSIS");

        // then
        assertThat(result).isEqualTo(NoteType.LYRICS_ANALYSIS);
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "free", "question", "", " "})
    void 유효하지_않은_문자열에_대해_예외를_발생시켜야_한다(String invalidType) {
        // when & then
        assertThatThrownBy(() -> NoteType.of(invalidType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid NoteType");
    }

    @Test
    void getType_메서드는_타입_문자열을_반환해야_한다() {
        // given
        NoteType noteType = NoteType.LYRICS_ANALYSIS;

        // when
        String result = noteType.getType();

        // then
        assertThat(result).isEqualTo("LYRICS_ANALYSIS");
    }
}
