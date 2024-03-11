package com.projectlyrics.server.domain.artist.service.impl;

import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;
import com.projectlyrics.server.domain.artist.dto.response.AddArtistResponse;
import com.projectlyrics.server.domain.artist.repository.ArtistRepository;
import com.projectlyrics.server.domain.artist.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArtistServiceImpl implements ArtistService {

  private final ArtistRepository artistRepository;

  @Transactional
  @Override
  public AddArtistResponse addArtist(AddArtistRequest request) {
    var savedArtist = artistRepository.save(request.toEntity());
    return AddArtistResponse.of(savedArtist.getId());
  }
}
