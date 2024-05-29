package com.projectlyrics.server.domain.common.util;

import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import org.springframework.data.domain.Slice;

public class PageUtils {

  public static <T extends CursorResponse> long getNextCursorOf(Slice<T> list) {
    if (list.isEmpty())
      return 0L;

    return list.getContent().getLast().getId() + 1;
  }
}
