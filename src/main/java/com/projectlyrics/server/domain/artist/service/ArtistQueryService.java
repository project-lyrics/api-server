package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.repository.QueryArtistRepository;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArtistQueryService {

  private final QueryArtistRepository queryArtistRepository;

  public ArtistGetResponse getArtist(Long artistId) {
    var artist = queryArtistRepository.findByIdAndNotDeleted(artistId)
        .orElseThrow(() -> new FeelinException(ErrorCode.ARTIST_NOT_FOUND));

    return ArtistGetResponse.from(artist);
  }

  public CursorBasePaginatedResponse<ArtistGetResponse> getArtistList(Long cursor, Pageable pageable) {
    var artistList = queryArtistRepository.findAllAndNotDeleted(cursor, pageable)
        .map(ArtistGetResponse::from);
    var nextCursor = artistList.getContent().getLast().id() + 1;

    return CursorBasePaginatedResponse.of(artistList, nextCursor, cursor);
  }
}
