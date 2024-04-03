package com.projectlyrics.server.domain.artist.application.usecase.query;

import com.projectlyrics.server.domain.artist.entity.ArtistEntity;

public interface GetArtistEntityByIdUseCase {

  ArtistEntity getArtistEntity(long artistId);

}
