package com.projectlyrics.server.domain.record.dto.request;

import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.record.domain.Record;

public record RecordGetResponse(
        Long id,
        String artistName,
        String artistProfileImageCdnLink
) implements CursorResponse {

    public static RecordGetResponse of(Record record) {
        return new RecordGetResponse(
                record.getId(),
                record.getArtist().getName(),
                record.getArtist().getImageUrl()
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
