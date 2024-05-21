package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.dto.response.ArtistAddResponse;
import com.projectlyrics.server.domain.artist.dto.response.ArtistUpdateResponse;

public interface ArtistCommandService {

  ArtistAddResponse addArtist(ArtistAddRequest request);

  ArtistUpdateResponse updateArtist(Long artistId, ArtistUpdateRequest request);

  void deleteArtist(Long artistId);
}
