package com.projectlyrics.server.domain.artist.application.persistence.command;


import com.projectlyrics.server.domain.artist.entity.ArtistEntity;

public interface CommandArtistRepository {

  ArtistEntity save(ArtistEntity entity);
}
