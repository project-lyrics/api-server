package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.common.dto.util.OffsetBasePaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArtistQueryService {

    private final ArtistQueryRepository artistQueryRepository;

    public Artist getArtistById(long artistId) {
        return artistQueryRepository.findById(artistId)
                .orElseThrow(ArtistNotFoundException::new);
    }

    public OffsetBasePaginatedResponse<ArtistGetResponse> getArtistList(Pageable pageable) {
        Slice<ArtistGetResponse> artistSlice = artistQueryRepository.findAll(pageable)
                .map(ArtistGetResponse::of);

        return OffsetBasePaginatedResponse.of(artistSlice);
    }

    public OffsetBasePaginatedResponse<ArtistGetResponse> searchArtists(String query, Pageable pageable) {
        Slice<ArtistGetResponse> searchedArtists = artistQueryRepository.findAllByQuery(query, pageable)
                .map(ArtistGetResponse::of);

        return OffsetBasePaginatedResponse.of(searchedArtists);
    }
}
