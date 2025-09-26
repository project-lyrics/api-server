package com.projectlyrics.server.domain.artist.repository.impl;

import com.projectlyrics.server.domain.artist.entity.ArtistMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AritstMongoDelegateRepository extends MongoRepository<ArtistMongo, Long> {
}