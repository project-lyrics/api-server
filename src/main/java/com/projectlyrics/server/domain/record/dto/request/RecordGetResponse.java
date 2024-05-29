package com.projectlyrics.server.domain.record.dto.request;

import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.record.domain.Record;

public record RecordGetResponse(
    Long id,
    String artistName,
    String artistEnglishName,
    String artistProfileImageCdnLink
) implements CursorResponse {

  public static RecordGetResponse of(Record record) {
    return new RecordGetResponse(
        record.getId(),
        record.getArtist().getName(),
        record.getArtist().getEnglishName(),
        record.getArtist().getProfileImageCdnLink()
    );
  }

  @Override
  public long getId() {
    return id;
  }
}
