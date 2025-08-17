package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.request.ArtistCreateRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.entity.ArtistCreate;
import com.projectlyrics.server.domain.artist.entity.ArtistMongo;
import com.projectlyrics.server.domain.artist.entity.ArtistUpdate;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistMongoCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArtistCommandService {

    private final ArtistCommandRepository artistCommandRepository;
    private final ArtistQueryRepository artistQueryRepository;
    private final ArtistMongoCommandRepository artistMongoCommandRepository;

    public Artist create(ArtistCreateRequest request) {
        Artist artist = artistCommandRepository.save(Artist.create(ArtistCreate.from(request)));

        try {
            artistMongoCommandRepository.save(ArtistMongo.of(artist));
        } catch (Exception e) {
            log.warn("MongoDB create failed for artist id {}: {}", artist.getId(), e.getMessage());
        }
        return artist;
    }

    public Artist update(Long artistId, ArtistUpdateRequest request) {
        Artist artist = artistQueryRepository.findById(artistId)
                .orElseThrow(ArtistNotFoundException::new);

        Artist updatedArtist = artist.update(ArtistUpdate.from(request));

        try {
            artistMongoCommandRepository.save(ArtistMongo.of(updatedArtist));
        } catch (Exception e) {
            log.warn("MongoDB update failed for artist id {}: {}", artistId, e.getMessage());
        }
        return updatedArtist;
    }

    public void delete(Long artistId, Long deletedBy) {
        Artist artist = artistQueryRepository.findById(artistId)
                .orElseThrow(ArtistNotFoundException::new);

        artist.delete(deletedBy, LocalDateTime.now(Clock.systemDefaultZone()));
        try {
            artistMongoCommandRepository.delete(ArtistMongo.of(artist));
        } catch (Exception e) {
            log.warn("MongoDB delete failed for artist id {}: {}", artist.getId(), e.getMessage());
        }
    }
}
