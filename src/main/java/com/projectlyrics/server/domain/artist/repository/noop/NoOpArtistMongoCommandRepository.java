package com.projectlyrics.server.domain.artist.repository.noop;

import com.projectlyrics.server.domain.artist.entity.ArtistMongo;
import com.projectlyrics.server.domain.artist.repository.ArtistMongoCommandRepository;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!prod & !dev")
public class NoOpArtistMongoCommandRepository implements ArtistMongoCommandRepository {

    @Override
    public ArtistMongo save(ArtistMongo artist) {
        return artist;
    }

    @Override
    public List<ArtistMongo> saveAll(List<ArtistMongo> artists) {
        return List.of();
    }

    @Override
    public void delete(ArtistMongo artist) {
    }

    @Override
    public void deleteAll() {
    }
}

