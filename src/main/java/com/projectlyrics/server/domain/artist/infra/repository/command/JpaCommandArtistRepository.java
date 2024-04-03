package com.projectlyrics.server.domain.artist.infra.repository.command;

import com.projectlyrics.server.domain.artist.application.persistence.command.CommandArtistRepository;
import com.projectlyrics.server.domain.artist.entity.ArtistEntity;
import org.springframework.data.repository.Repository;

public interface JpaCommandArtistRepository extends Repository<ArtistEntity, Long>, CommandArtistRepository {
}
