package com.projectlyrics.server.domain.artist.repository.noop;

import com.projectlyrics.server.domain.artist.repository.ArtistMongoQueryRepository;
import com.projectlyrics.server.domain.common.dto.util.IdsWithHasNext;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"!dev", "!prod"})
public class NoOpArtistMongoQueryRepository implements ArtistMongoQueryRepository {
    @Override
    public IdsWithHasNext searchArtistIdsByName(String query, int offset, int limit) {
        return new IdsWithHasNext(List.of(), false);
    }
}
