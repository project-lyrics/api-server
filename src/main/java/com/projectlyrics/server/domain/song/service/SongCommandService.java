package com.projectlyrics.server.domain.song.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.song.dto.request.SongCreateRequest;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.entity.SongCreate;
import com.projectlyrics.server.domain.song.entity.SongMongo;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.song.repository.SongMongoCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SongCommandService {

    private final SongCommandRepository songCommandRepository;
    private final ArtistQueryRepository artistQueryRepository;
    private final SongMongoCommandRepository songMongoCommandRepository;

    public Song create(SongCreateRequest request) {
        Artist artist = artistQueryRepository.findById(request.artistId())
                .orElseThrow(ArtistNotFoundException::new);
        Song song = songCommandRepository.save(Song.create(SongCreate.from(request, artist)));

        try {
            songMongoCommandRepository.save(SongMongo.of(song));
        } catch (Exception e) {
            log.warn("MongoDB create failed for song id {}: {}", song.getId(), e.getMessage());
        }
        return song;
    }
}
