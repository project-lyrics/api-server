package com.projectlyrics.server.domain.common.util;

import com.projectlyrics.server.global.exception.DomainInvalidUrlException;
import com.projectlyrics.server.global.exception.DomainNullFieldException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainUtilsTest {

    @Test()
    void Enum_타입의_필드에_대해_null_체크를_한다() {
        // given
        Enum nullEnum = null;;

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
                .isInstanceOf(DomainNullFieldException.class);
    }

    @Test()
    void URL_타입의_필드에_대해_유효성_체크를_한다() {
        // given
        String invalidUrl = "invalidUrl";

        String httpUrl = "http://validUrl.com";
        String httpsUrl = "https://validUrl.com";

        // when
        assertThatThrownBy(() -> DomainUtils.checkUrl(invalidUrl))
                .isInstanceOf(DomainInvalidUrlException.class);

        DomainUtils.checkUrl(httpUrl);
        DomainUtils.checkUrl(httpsUrl);
    }
}