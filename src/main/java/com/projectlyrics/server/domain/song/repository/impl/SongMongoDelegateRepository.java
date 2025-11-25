package com.projectlyrics.server.domain.song.repository.impl;

import com.projectlyrics.server.domain.song.entity.SongMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongMongoDelegateRepository extends MongoRepository<SongMongo, Long> {
}

