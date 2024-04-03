package com.projectlyrics.server.domain.artist.service.query;

import com.projectlyrics.server.domain.artist.application.dto.GetSingleArtistMetaDto;
import com.projectlyrics.server.domain.artist.application.persistence.query.QueryArtistRepository;
import com.projectlyrics.server.domain.artist.application.usecase.query.GetArtistEntityByIdUseCase;
import com.projectlyrics.server.domain.artist.application.usecase.query.GetSingleArtistByIdMetaUseCase;
import com.projectlyrics.server.domain.artist.entity.ArtistEntity;
import com.projectlyrics.server.global.error_code.ErrorCode;
import com.projectlyrics.server.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class QueryArtistService implements GetArtistEntityByIdUseCase, GetSingleArtistByIdMetaUseCase {

  private final QueryArtistRepository queryArtistRepository;

  @Override
  public ArtistEntity getArtistEntity(long artistId) {
    return queryArtistRepository.findById(artistId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ARTIST_NOT_FOUND));
  }

  @Override
  public GetSingleArtistMetaDto.Response getMetaById(long artistId) {
    return GetSingleArtistMetaDto.Response.from(this.getArtistEntity(artistId));
  }
}
