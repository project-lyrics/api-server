package com.projectlyrics.server.domain.favoriteartist.repository;

import com.projectlyrics.server.domain.favoriteartist.entity.FavoriteArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteArtistCommandRepository extends JpaRepository<FavoriteArtist, Long> {
}
