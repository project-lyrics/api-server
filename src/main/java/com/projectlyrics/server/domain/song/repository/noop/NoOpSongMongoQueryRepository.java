package com.projectlyrics.server.domain.song.repository.noop;

import com.projectlyrics.server.domain.common.dto.util.IdsWithHasNext;
import com.projectlyrics.server.domain.song.repository.SongMongoQueryRepository;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile({"!dev", "!prod"})
@Repository
public class NoOpSongMongoQueryRepository implements SongMongoQueryRepository {
    @Override
    public IdsWithHasNext searchSongsByName(String query, int offset, int limit) {
        return new IdsWithHasNext(List.of(), false);
    }

    @Override
    public List<Long> findAllIdByIdIn(List<Long> ids) {
        return List.of();
    }
}