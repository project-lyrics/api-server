package com.projectlyrics.server.domain.song.repository;

import com.projectlyrics.server.domain.song.entity.SongMongo;
import java.util.List;

public interface SongMongoCommandRepository {
    SongMongo save(SongMongo song);

    List<SongMongo> saveAll(List<SongMongo> songs);

    void deleteAll();
}
