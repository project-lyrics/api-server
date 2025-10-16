package com.projectlyrics.server.domain.song.repository.noop;

import com.projectlyrics.server.domain.common.dto.util.IdsWithHasNext;
import com.projectlyrics.server.domain.song.repository.SongMongoQueryRepository;
import java.util.List;


public class NoOpSongMongoQueryRepository implements SongMongoQueryRepository {
    @Override
    public IdsWithHasNext searchSongsByName(String query, int offset, int limit) {
        return new IdsWithHasNext(List.of(), false);
    }

    @Override
    public List<Long> findAllByIdsIn(List<Long> ids) {
        return List.of();
    }
}