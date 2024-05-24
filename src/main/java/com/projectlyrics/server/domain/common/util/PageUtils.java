package com.projectlyrics.server.domain.common.util;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import org.springframework.data.domain.Slice;

public class PageUtils {

  public static Long getNextCursorOf(Slice<ArtistGetResponse> list) {
    if (list.isEmpty())
      return 0L;

    return list.getContent().getLast().id() + 1;
  }
}
