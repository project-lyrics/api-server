package com.projectlyrics.server.domain.artist.application.usecase.command;

import com.projectlyrics.server.domain.artist.application.dto.AddArtistDto;

public interface AddArtistUseCase {

  AddArtistDto.Response addArtist(AddArtistDto.Request request);
}
