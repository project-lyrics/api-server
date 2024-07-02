package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.dto.response.ArtistAddResponse;
import com.projectlyrics.server.domain.artist.dto.response.ArtistUpdateResponse;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;

import java.time.Clock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ArtistCommandService {

    private final ArtistCommandRepository artistCommandRepository;
    private final ArtistQueryRepository artistQueryRepository;

    public ArtistAddResponse addArtist(ArtistAddRequest request) {
        Artist savedArtist = artistCommandRepository.save(Artist.from(request));
        return ArtistAddResponse.of(savedArtist.getId());
    }

    public ArtistUpdateResponse updateArtist(Long artistId, ArtistUpdateRequest request) {
        Artist artist = artistQueryRepository.findByIdAndNotDeleted(artistId)
                .orElseThrow(ArtistNotFoundException::new);

        artist.updateIfNotBlank(request.name(), artist::updateName);
        artist.updateIfNotBlank(request.profileImageCdnLink(), artist::updateImageUrl);

        return ArtistUpdateResponse.from(artist);
    }

    public void deleteArtist(Long artistId) {
        Artist artist = artistQueryRepository.findByIdAndNotDeleted(artistId)
                .orElseThrow(ArtistNotFoundException::new);

        artist.delete(artistId, Clock.systemDefaultZone());
    }
}
