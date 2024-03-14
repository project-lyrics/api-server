package com.projectlyrics.server.domain.artist.service;


import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;
import com.projectlyrics.server.domain.artist.dto.request.UpdateArtistRequest;
import com.projectlyrics.server.domain.artist.dto.response.AddArtistResponse;
import com.projectlyrics.server.domain.artist.dto.response.GetArtistResponse;
import com.projectlyrics.server.domain.artist.dto.response.UpdateArtistResponse;

public interface ArtistService {

  AddArtistResponse addArtist(AddArtistRequest request);

  UpdateArtistResponse updateArtist(Long artistId, UpdateArtistRequest request);

  void deleteArtist(Long artistId);

  GetArtistResponse getArtist(Long artistId);
}
