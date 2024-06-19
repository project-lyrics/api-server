package com.projectlyrics.server.domain.common.util;

import com.projectlyrics.server.global.exception.DomainInvalidUrlException;
import com.projectlyrics.server.global.exception.DomainNullFieldException;

public class DomainUtils {

    public static <T extends Enum> void checkEnum(T enumField) {
        if (enumField == null)
            throw new DomainNullFieldException();
    }

    public static void checkString(String string) {
        if (string == null || string.isBlank())
            throw new DomainNullFieldException();
    }

    public static void checkUrl(String url) {
        if (!url.startsWith("https://") || !url.startsWith("http://"))
            throw new DomainInvalidUrlException();
    }
}
