package com.projectlyrics.server.domain.song.repository.impl;

import com.projectlyrics.server.domain.song.entity.SongMongo;
import com.projectlyrics.server.domain.song.repository.SongMongoCommandRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class SongMongoCommandRepositoryImpl implements SongMongoCommandRepository {

    private final SongMongoDelegateRepository delegate;

    @Override
    public SongMongo save(SongMongo song) {
        return delegate.save(song);
    }

    @Override
    public <S extends SongMongo> List<S> saveAll(Iterable<S> songs) {
        return delegate.saveAll(songs);
    }

    @Override
    public void deleteAll() {
        delegate.deleteAll();
    }
}

