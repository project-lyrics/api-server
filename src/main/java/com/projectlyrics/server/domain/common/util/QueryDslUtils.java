package com.projectlyrics.server.domain.common.util;

import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.List;

import com.querydsl.core.types.dsl.NumberPath;
import org.springframework.data.domain.Pageable;

public class QueryDslUtils {

    public static BooleanExpression gtCursorId(Long cursor, NumberPath<Long> id) {
        return cursor == null ? null : id.gt(cursor);
    }

    public static <T> boolean checkIfHasNext(Pageable pageable, List<T> content) {
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());

            return true;
        }

        return false;
    }
}
