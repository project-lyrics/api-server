package com.projectlyrics.server.domain.song.repository;

import com.projectlyrics.server.domain.song.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongCommandRepository extends JpaRepository<Song, Long> {
}
