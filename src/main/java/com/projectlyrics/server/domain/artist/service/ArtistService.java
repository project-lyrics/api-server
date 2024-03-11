package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.ArtistDto;
import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;

public interface ArtistService {

  ArtistDto addArtist(AddArtistRequest request);
}
