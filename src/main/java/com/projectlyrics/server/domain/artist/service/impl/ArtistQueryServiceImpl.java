package com.projectlyrics.server.domain.artist.service.impl;

import com.projectlyrics.server.domain.artist.dto.response.GetArtistResponse;
import com.projectlyrics.server.domain.artist.repository.CommandQueryArtistRepository;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.domain.common.dto.CursorBasePaginatedResponse;
import com.projectlyrics.server.global.error_code.ErrorCode;
import com.projectlyrics.server.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArtistQueryServiceImpl implements ArtistQueryService {

  private final CommandQueryArtistRepository commandArtistRepository;

  @Override
  public GetArtistResponse getArtist(Long artistId) {
    var artist = commandArtistRepository.findByIdAndNotDeleted(artistId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ARTIST_NOT_FOUND));

    return GetArtistResponse.from(artist);
  }

  @Override
  public CursorBasePaginatedResponse<GetArtistResponse> getArtistList(Long cursor, Pageable pageable) {
    var artistList = commandArtistRepository.findAllAndNotDeleted(cursor, pageable).map(GetArtistResponse::from);
    var nextCursor = artistList.getContent().getLast().id() + 1;

    return CursorBasePaginatedResponse.of(artistList, cursor, nextCursor);
  }
}
