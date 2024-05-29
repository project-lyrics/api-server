package com.projectlyrics.server.domain.record.dto.response;

import com.projectlyrics.server.domain.record.domain.Record;

public record RecordAddResponse(
    Long id
) {

  public static RecordAddResponse of(Record record) {
    return new RecordAddResponse(record.getId());
  }
}
