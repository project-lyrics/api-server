package com.projectlyrics.server.domain.artist.repository;

import com.projectlyrics.server.domain.common.dto.util.IdsWithHasNext;

public interface ArtistMongoQueryRepository {
    IdsWithHasNext searchArtistIdsByName(String query, int offset, int limit);
}
