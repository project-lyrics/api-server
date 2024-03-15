package com.projectlyrics.server.domain.artist.service.impl;

import com.projectlyrics.server.domain.artist.dto.response.GetArtistResponse;
import com.projectlyrics.server.domain.artist.repository.CommandQueryArtistRepository;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.global.error_code.ErrorCode;
import com.projectlyrics.server.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
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
}
