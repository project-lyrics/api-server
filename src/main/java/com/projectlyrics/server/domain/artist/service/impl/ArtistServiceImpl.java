package com.projectlyrics.server.domain.artist.service.impl;

import com.projectlyrics.server.domain.artist.dto.AddArtistRequest;
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
  public Long addArtist(AddArtistRequest request) {
    var savedArtist = artistRepository.save(request.toEntity());
    return savedArtist.getId();
  }
}
