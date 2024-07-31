package com.projectlyrics.server.domain.common.util;

import com.projectlyrics.server.global.exception.DomainEmptyException;
import com.projectlyrics.server.global.exception.DomainInvalidUrlException;
import com.projectlyrics.server.global.exception.DomainNullFieldException;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class DomainUtils {

    public static  <T extends Enum> void checkEnum(T enumField) {
        checkNull(enumField);
    }

    public static void checkString(String string) {
        checkNull(string);

        if (string.isEmpty())
            throw new DomainEmptyException();
    }

    public static void checkUrl(String url) {
        checkString(url);

        try {
            new URI(url).toURL();
        } catch (Exception e) {
            throw new DomainInvalidUrlException();
        }
    }

    public static void checkNull(Object object) {
        if (Objects.isNull(object)) {
            throw new DomainNullFieldException();
        }
    }
}
