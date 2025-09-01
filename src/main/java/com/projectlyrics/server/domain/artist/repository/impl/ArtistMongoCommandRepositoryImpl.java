package com.projectlyrics.server.domain.artist.repository.impl;

import com.projectlyrics.server.domain.artist.entity.ArtistMongo;
import com.projectlyrics.server.domain.artist.repository.ArtistMongoCommandRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class ArtistMongoCommandRepositoryImpl implements ArtistMongoCommandRepository {

    private final AritstMongoDelegateRepository delegate;

    @Override
    public ArtistMongo save(ArtistMongo artist) {
        return delegate.save(artist);
    }

    @Override
    public <S extends ArtistMongo> List<S> saveAll(Iterable<S> artists) {
        return delegate.saveAll(artists);
    }

    @Override
    public void delete(ArtistMongo artist) {
        delegate.delete(artist);
    }

    @Override
    public void deleteAll() {
        delegate.deleteAll();
    }
}
