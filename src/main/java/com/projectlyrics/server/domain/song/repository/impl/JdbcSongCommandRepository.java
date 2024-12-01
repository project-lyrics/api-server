package com.projectlyrics.server.domain.song.repository.impl;

import com.projectlyrics.server.domain.common.entity.enumerate.EntityStatusEnum;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
public class JdbcSongCommandRepository implements SongCommandRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String insertQuery = "INSERT INTO songs (artist_id, spotify_id, name, release_date, album_name, image_url, created_at, created_by, status, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String insertQueryWithId = "INSERT INTO songs (id, artist_id, spotify_id, name, release_date, album_name, image_url, created_at, created_by, status, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ? ,? ,?, ?)";

    @Override
    public Song save(Song song) {
        if (Objects.nonNull(song.getId())) {
            jdbcTemplate.update(
                    insertQueryWithId,
                    song.getId(),
                    song.getArtist().getId(),
                    song.getSpotifyId(),
                    song.getName(),
                    song.getReleaseDate(),
                    song.getAlbumName(),
                    song.getImageUrl()
            );
        }

        else {
            jdbcTemplate.update(
                    insertQuery,
                    song.getArtist().getId(),
                    song.getSpotifyId(),
                    song.getName(),
                    song.getReleaseDate(),
                    song.getAlbumName(),
                    song.getImageUrl()
            );
        }

        return song;
    }

    @Override
    public void saveAll(List<Song> songs) {
        jdbcTemplate.batchUpdate(
                insertQuery,
                songs,
                songs.size(),
                (ps, song) -> {
                    ps.setLong(1, song.getArtist().getId());
                    ps.setString(2, song.getSpotifyId());
                    ps.setString(3, song.getName());
                    ps.setDate(4, java.sql.Date.valueOf(song.getReleaseDate()));
                    ps.setString(5, song.getAlbumName());
                    ps.setString(6, song.getImageUrl());
                    ps.setString(7, LocalDateTime.now().toString());
                    ps.setInt(8, 0);
                    ps.setString(9, EntityStatusEnum.IN_USE.toString());
                    ps.setInt(10, 0);
                }
        );
    }
}
