package com.projectlyrics.server.domain.bookmark.dto.response;

public record BookmarkResponse(
        Long noteId
) {

    public static BookmarkResponse of(Long noteId) {
        return new BookmarkResponse(noteId);
    }
}
