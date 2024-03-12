package com.projectlyrics.server.domain.artist.service;


import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;
import com.projectlyrics.server.domain.artist.dto.response.AddArtistResponse;

public interface ArtistService {

  AddArtistResponse addArtist(AddArtistRequest request);
}
