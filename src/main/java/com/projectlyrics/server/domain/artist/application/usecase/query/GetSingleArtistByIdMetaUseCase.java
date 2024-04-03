package com.projectlyrics.server.domain.artist.application.usecase.query;

import com.projectlyrics.server.domain.artist.application.dto.GetSingleArtistMetaDto;

public interface GetSingleArtistByIdMetaUseCase {

  GetSingleArtistMetaDto.Response getMetaById(long artistId);
}
