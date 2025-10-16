package com.projectlyrics.server.domain.song.repository.noop;

import com.projectlyrics.server.domain.song.entity.SongMongo;
import com.projectlyrics.server.domain.song.repository.SongMongoCommandRepository;
import java.util.List;

public class NoOpSongMongoCommandRepository implements SongMongoCommandRepository {

    @Override
    public SongMongo save(SongMongo song) {
        return song;
    }

    @Override
    public List<SongMongo> saveAll(List<SongMongo> songs) {
        return List.of();
    }

    @Override
    public void deleteAll() {
    }
}
