package com.projectlyrics.server.domain.song.repository;

import com.projectlyrics.server.domain.common.dto.util.IdsWithHasNext;
import java.util.List;

public interface SongMongoQueryRepository {
    IdsWithHasNext searchSongsByName(String query, int offset, int limit);

    List<Long> findAllIdByIdIn(List<Long> ids);
}
