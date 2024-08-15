package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArtistQueryService {

    private final ArtistQueryRepository artistQueryRepository;

    public Artist getArtistById(long artistId) {
        return artistQueryRepository.findById(artistId)
                .orElseThrow(ArtistNotFoundException::new);
    }

    public CursorBasePaginatedResponse<ArtistGetResponse> getArtistList(Long cursor, Pageable pageable) {
        Slice<ArtistGetResponse> artistSlice = artistQueryRepository.findAllAndNotDeleted(cursor, pageable)
                .map(ArtistGetResponse::of);

        return CursorBasePaginatedResponse.of(artistSlice);
    }

    public CursorBasePaginatedResponse<ArtistGetResponse> searchArtists(String query, Long cursor, Pageable pageable) {
        Slice<ArtistGetResponse> searchedArtists = artistQueryRepository.findAllByQueryAndNotDeleted(
                        query,
                        cursor,
                        pageable
                )
                .map(ArtistGetResponse::of);

        return CursorBasePaginatedResponse.of(searchedArtists);
    }

    public List<Artist> getArtistsByIds(List<Long> artistIds) {
        return artistQueryRepository.findAllByIds(artistIds);
    }
}
