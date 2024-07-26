package com.projectlyrics.server.domain.note.entity;

import com.projectlyrics.server.domain.note.dto.request.NoteUpdateRequest;
import com.projectlyrics.server.global.exception.DomainNullFieldException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class NoteUpdateTest {

    @Test
    void 요청_파라미터와_작성자_객체로부터_노트_수정_객체를_생성해야_한다() {
        // given
        NoteUpdateRequest request = new NoteUpdateRequest(
                "content",
                "lyrics",
                NoteBackground.WHITE,
                NoteStatus.PUBLISHED
        );

        // when
        NoteUpdate result = NoteUpdate.from(request);

        // then
        assertAll(
                () -> assertThat(result.content()).isEqualTo(request.content()),
                () -> assertThat(result.lyrics()).isEqualTo(request.lyrics()),
                () -> assertThat(result.background()).isEqualTo(request.background()),
                () -> assertThat(result.status()).isEqualTo(request.status())
        );
    }

    @Test
    void 널_노트_상태에_대해_예외를_발생시켜야_한다() {
        // given
        NoteUpdateRequest request = new NoteUpdateRequest(
                "content",
                "lyrics",
                NoteBackground.WHITE,
                null
        );

        // when & then
        assertThatThrownBy(() -> NoteUpdate.from(request))
                .isInstanceOf(DomainNullFieldException.class);
    }
}