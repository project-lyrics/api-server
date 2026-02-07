package com.projectlyrics.server.global.configuration;

import com.projectlyrics.server.domain.artist.repository.ArtistMongoCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistMongoQueryRepository;
import com.projectlyrics.server.domain.artist.repository.impl.AritstMongoDelegateRepository;
import com.projectlyrics.server.domain.artist.repository.impl.ArtistMongoCommandRepositoryImpl;
import com.projectlyrics.server.domain.artist.repository.impl.ArtistMongoQueryRepositoryImpl;
import com.projectlyrics.server.domain.artist.repository.noop.NoOpArtistMongoCommandRepository;
import com.projectlyrics.server.domain.artist.repository.noop.NoOpArtistMongoQueryRepository;
import com.projectlyrics.server.domain.song.repository.SongMongoCommandRepository;
import com.projectlyrics.server.domain.song.repository.SongMongoQueryRepository;
import com.projectlyrics.server.domain.song.repository.impl.SongMongoCommandRepositoryImpl;
import com.projectlyrics.server.domain.song.repository.impl.SongMongoDelegateRepository;
import com.projectlyrics.server.domain.song.repository.impl.SongMongoQueryRepositoryImpl;
import com.projectlyrics.server.domain.song.repository.noop.NoOpSongMongoCommandRepository;
import com.projectlyrics.server.domain.song.repository.noop.NoOpSongMongoQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Slf4j
@Configuration
public class MongoConfig {

    private boolean isMongoAvailable(MongoTemplate mongoTemplate) {
        try {
            /*
            Mongo DB 연결 가능할 때까지 disabled 처리합니다.
            mongoTemplate.getDb().runCommand(new org.bson.Document("ping", 1));
            log.info("MongoDB 연결 성공");
            */
            return false;
        } catch (Exception e) {
            log.warn("MongoDB 연결 실패 — NoOp 리포지토리로 대체됩니다. 이유: {}", e.getMessage());
            return false;
        }
    }

    @Bean
    public SongMongoCommandRepository songMongoCommandRepository(MongoTemplate mongoTemplate,
                                                                 SongMongoDelegateRepository delegate) {
        if (isMongoAvailable(mongoTemplate)) {
            return new SongMongoCommandRepositoryImpl(delegate);
        }
        return new NoOpSongMongoCommandRepository();
    }

    @Bean
    public SongMongoQueryRepository songMongoQueryRepository(MongoTemplate mongoTemplate) {
        if (isMongoAvailable(mongoTemplate)) {
            return new SongMongoQueryRepositoryImpl(mongoTemplate);
        }
        return new NoOpSongMongoQueryRepository();
    }

    @Bean
    public ArtistMongoCommandRepository artistMongoCommandRepository(MongoTemplate mongoTemplate,
                                                                     AritstMongoDelegateRepository delegate) {
        if (isMongoAvailable(mongoTemplate)) {
            return new ArtistMongoCommandRepositoryImpl(delegate);
        }
        return new NoOpArtistMongoCommandRepository();
    }

    @Bean
    public ArtistMongoQueryRepository artistMongoQueryRepository(MongoTemplate mongoTemplate) {
        if (isMongoAvailable(mongoTemplate)) {
            return new ArtistMongoQueryRepositoryImpl(mongoTemplate);
        }
        return new NoOpArtistMongoQueryRepository();
    }
}
