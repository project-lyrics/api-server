package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.response.GetArtistResponse;

public interface ArtistQueryService {

  GetArtistResponse getArtist(Long artistId);
}
