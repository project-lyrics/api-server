package com.projectlyrics.server.domain.artist.repository;

import com.projectlyrics.server.domain.artist.entity.ArtistMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistMongoCommandRepository extends MongoRepository<ArtistMongo, Long> {
}
