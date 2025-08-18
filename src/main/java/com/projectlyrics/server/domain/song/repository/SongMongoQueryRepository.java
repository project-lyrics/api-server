package com.projectlyrics.server.domain.song.repository;

import com.projectlyrics.server.domain.common.dto.util.IdsWithHasNext;

public interface SongMongoQueryRepository {
    IdsWithHasNext searchSongsByName(String query, int offset, int limit);
}
