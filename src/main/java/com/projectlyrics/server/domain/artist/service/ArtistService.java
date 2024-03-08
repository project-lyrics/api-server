package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.AddArtistRequest;

public interface ArtistService {

  Long addArtist(AddArtistRequest request);
}
