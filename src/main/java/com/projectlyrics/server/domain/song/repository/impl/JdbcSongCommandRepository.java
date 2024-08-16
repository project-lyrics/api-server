package com.projectlyrics.server.domain.song.repository.impl;

import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class JdbcSongCommandRepository implements SongCommandRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String insertQuery = "INSERT INTO songs (id, artist_id, spotify_id, name, release_date, album_name, image_url) VALUES (?, ?, ?, ?, ?, ?, ?)";

    @Override
    public Song save(Song song) {
        jdbcTemplate.update(
                insertQuery,
                song.getId(),
                song.getArtist().getId(),
                song.getSpotifyId(),
                song.getName(),
                song.getReleaseDate(),
                song.getAlbumName(),
                song.getImageUrl()
        );

        return song;
    }

    @Override
    public void saveAll(List<Song> songs) {
        jdbcTemplate.batchUpdate(
                insertQuery,
                songs,
                songs.size(),
                (ps, song) -> {
                    ps.setLong(1, song.getId());
                    ps.setLong(2, song.getArtist().getId());
                    ps.setString(3, song.getSpotifyId());
                    ps.setString(4, song.getName());
                    ps.setDate(5, java.sql.Date.valueOf(song.getReleaseDate()));
                    ps.setString(6, song.getAlbumName());
                    ps.setString(7, song.getImageUrl());
                }
        );
    }
}
