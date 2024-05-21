package com.projectlyrics.server.domain.artist.service.impl;

import com.projectlyrics.server.domain.artist.dto.response.GetArtistResponse;
import com.projectlyrics.server.domain.artist.repository.QueryArtistRepository;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.domain.common.dto.CursorBasePaginatedResponse;
import com.projectlyrics.server.global.exception.FeelinException;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArtistQueryServiceImpl implements ArtistQueryService {

  private final QueryArtistRepository queryArtistRepository;

  @Override
  public GetArtistResponse getArtist(Long artistId) {
    var artist = queryArtistRepository.findByIdAndNotDeleted(artistId)
        .orElseThrow(() -> new FeelinException(ErrorCode.ARTIST_NOT_FOUND));

    return GetArtistResponse.from(artist);
  }

  @Override
  public CursorBasePaginatedResponse<GetArtistResponse> getArtistList(Long cursor, Pageable pageable) {
    var artistList = queryArtistRepository.findAllAndNotDeleted(cursor, pageable).map(GetArtistResponse::from);
    var nextCursor = artistList.getContent().getLast().id() + 1;

    return CursorBasePaginatedResponse.of(artistList, cursor, nextCursor);
  }
}
