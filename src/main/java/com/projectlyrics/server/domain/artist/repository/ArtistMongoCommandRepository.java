package com.projectlyrics.server.domain.artist.repository;

import com.projectlyrics.server.domain.artist.entity.ArtistMongo;
import java.util.List;

public interface ArtistMongoCommandRepository {
    ArtistMongo save(ArtistMongo artist);

    <S extends ArtistMongo> List<S> saveAll(Iterable<S> artists);

    void delete(ArtistMongo artist);

    void deleteAll();
}

