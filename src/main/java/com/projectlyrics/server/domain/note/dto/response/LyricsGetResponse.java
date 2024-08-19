package com.projectlyrics.server.domain.note.dto.response;

import com.projectlyrics.server.domain.note.entity.Lyrics;

public record LyricsGetResponse(
        String lyrics,
        String background
) {

    public static LyricsGetResponse from(Lyrics lyrics) {
        if (lyrics == null) {
            return null;
        }

        return new LyricsGetResponse(
                lyrics.getContent(),
                lyrics.getBackground().name()
        );
    }
}
