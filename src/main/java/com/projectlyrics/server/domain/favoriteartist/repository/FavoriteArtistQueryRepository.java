package com.projectlyrics.server.domain.favoriteartist.repository;

import com.projectlyrics.server.domain.favoriteartist.entity.FavoriteArtist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface FavoriteArtistQueryRepository {
    Slice<FavoriteArtist> findAllByUserId(Long userId, Long cursorId, Pageable pageable);

    List<FavoriteArtist> findAllByUserIdFetchArtist(Long userId);
}
