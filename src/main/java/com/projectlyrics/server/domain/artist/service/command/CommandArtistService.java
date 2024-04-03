package com.projectlyrics.server.domain.artist.service.command;

import com.projectlyrics.server.domain.artist.application.dto.AddArtistDto;
import com.projectlyrics.server.domain.artist.application.dto.AddArtistDto.Response;
import com.projectlyrics.server.domain.artist.application.persistence.command.CommandArtistRepository;
import com.projectlyrics.server.domain.artist.application.usecase.command.AddArtistUseCase;
import com.projectlyrics.server.domain.artist.entity.ArtistEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class CommandArtistService implements AddArtistUseCase {

  private final CommandArtistRepository commandArtistRepository;

  @Override
  public AddArtistDto.Response addArtist(AddArtistDto.Request request) {
    ArtistEntity savedArtist = commandArtistRepository.save(request.toEntity());
    return new Response(savedArtist.getId());
  }
}
