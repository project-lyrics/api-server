package com.projectlyrics.server.domain.song.repository.noop;

import com.projectlyrics.server.domain.song.entity.SongMongo;
import com.projectlyrics.server.domain.song.repository.SongMongoCommandRepository;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"!dev", "!prod"})
public class NoOpSongMongoCommandRepository implements SongMongoCommandRepository {

    @Override
    public SongMongo save(SongMongo song) {
        return song;
    }

    @Override
    public <S extends SongMongo> List<S> saveAll(Iterable<S> songs) {
        return List.of();
    }

    @Override
    public void deleteAll() {
    }
}
