package com.projectlyrics.server.domain.common.util;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.entity.QArtist;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import org.springframework.data.domain.Pageable;

public class QueryDslUtils {

  public static BooleanExpression goeCursorId(Long cursor) {
    return cursor == null ? null : QArtist.artist.id.goe(cursor);
  }

  public static boolean checkIfHasNext(Pageable pageable, List<Artist> content) {
    if (content.size() > pageable.getPageSize()) {
      content.remove(pageable.getPageSize());

      return true;
    }

    return false;
  }
}
