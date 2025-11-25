package com.projectlyrics.server.domain.artist.repository;

import com.projectlyrics.server.domain.artist.entity.ArtistMongo;
import java.util.List;

public interface ArtistMongoCommandRepository {
    ArtistMongo save(ArtistMongo artist);

    List<ArtistMongo> saveAll(List<ArtistMongo> artists);

    void delete(ArtistMongo artist);

    void deleteAll();
}

