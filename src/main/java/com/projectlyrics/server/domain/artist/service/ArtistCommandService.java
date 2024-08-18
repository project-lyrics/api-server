package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.request.ArtistCreateRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.entity.ArtistCreate;
import com.projectlyrics.server.domain.artist.entity.ArtistUpdate;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;

import java.time.Clock;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ArtistCommandService {

    private final ArtistCommandRepository artistCommandRepository;
    private final ArtistQueryRepository artistQueryRepository;

    public Artist create(ArtistCreateRequest request) {
        return artistCommandRepository.save(Artist.create(ArtistCreate.from(request)));
    }

    public Artist update(Long artistId, ArtistUpdateRequest request) {
        Artist artist = artistQueryRepository.findById(artistId)
                .orElseThrow(ArtistNotFoundException::new);

        return artist.update(ArtistUpdate.from(request));
    }

    public void delete(Long artistId, Long deletedBy) {
        Artist artist = artistQueryRepository.findById(artistId)
                .orElseThrow(ArtistNotFoundException::new);

        artist.delete(deletedBy, LocalDateTime.now(Clock.systemDefaultZone()));
    }
}
