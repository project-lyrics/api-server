package com.projectlyrics.server.domain.common.util;

import com.projectlyrics.server.global.exception.DomainEmptyException;
import com.projectlyrics.server.global.exception.DomainInvalidUrlException;
import com.projectlyrics.server.global.exception.DomainNullFieldException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class DomainUtilsTest {

    @Test()
    void Enum_타입의_필드에_대해_null_체크를_한다() {
        // given
        Enum nullEnum = null;
        ;

        // when
        assertThatThrownBy(() -> DomainUtils.checkEnum(nullEnum))
                .isInstanceOf(DomainNullFieldException.class);
    }

    @Test()
    void 문자열_타입의_필드에_대해_null_또는_빈값_체크를_한다() {
        // given
        String nullString = null;
        String emptyString = "";

        // when
        assertThatThrownBy(() -> DomainUtils.checkString(nullString))
                .isInstanceOf(DomainNullFieldException.class);
        assertThatThrownBy(() -> DomainUtils.checkString(emptyString))
                .isInstanceOf(DomainEmptyException.class);
    }

    @Test()
    void URL_타입의_필드에_대해_유효성_체크를_한다() {
        // given
        String invalidUrl = "invalidUrl";

        String httpUrl = "http://validUrl.com";
        String httpsUrl = "https://validurl.com";

        // when
        assertThatThrownBy(() -> DomainUtils.checkUrl(invalidUrl))
                .isInstanceOf(DomainInvalidUrlException.class);

        assertThatNoException().isThrownBy(() -> DomainUtils.checkUrl(httpUrl));
        assertThatNoException().isThrownBy(() -> DomainUtils.checkUrl(httpsUrl));
    }

    @Test
    void LocalDateTime_객체에_대해_포맷팅을_한다() {
        // given
        LocalDateTime time = LocalDateTime.of(2021, 1, 1, 0, 0);

        // when
        String formattedTime = DomainUtils.formatTime(time);

        // then
        assertThat(formattedTime).isEqualTo("2021.01.01 00:00");
    }
}