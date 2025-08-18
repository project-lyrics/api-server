package com.projectlyrics.server.domain.song.repository;

import com.projectlyrics.server.domain.song.entity.SongMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongMongoCommandRepository extends MongoRepository<SongMongo, Long> {
}
