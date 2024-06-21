package com.projectlyrics.server.domain.common.util;

import com.projectlyrics.server.global.exception.DomainEmptyException;
import com.projectlyrics.server.global.exception.DomainInvalidUrlException;
import com.projectlyrics.server.global.exception.DomainNullFieldException;

import java.util.Objects;

public class DomainUtils {

    public static <T extends Enum> void checkEnum(T enumField) {
        if (isNull(enumField))
            throw new DomainNullFieldException();
    }

    public static void checkString(String string) {
        if (isNull(string))
            throw new DomainNullFieldException();

        if (string.isEmpty())
            throw new DomainEmptyException();
    }

    public static void checkUrl(String url) {
        checkString(url);

        if (!url.startsWith("https://") && !url.startsWith("http://"))
            throw new DomainInvalidUrlException();
    }

    private static boolean isNull(Object object) {
        return Objects.isNull(object);
    }
}
