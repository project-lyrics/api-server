package com.projectlyrics.server.domain.song.repository.impl;

import com.projectlyrics.server.domain.common.entity.enumerate.EntityStatusEnum;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

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
                    song.getImageUrl(),
                    LocalDateTime.now().toString(),
                    0,
                    EntityStatusEnum.IN_USE.toString(),
                    0
            );
        } else {
            jdbcTemplate.update(
                    insertQuery,
                    song.getArtist().getId(),
                    song.getSpotifyId(),
                    song.getName(),
                    song.getReleaseDate(),
                    song.getAlbumName(),
                    song.getImageUrl(),
                    LocalDateTime.now().toString(),
                    0,
                    EntityStatusEnum.IN_USE.toString(),
                    0
            );
        }

        return song;
    }

    @Override
    public List<Song> saveAll(List<Song> songs) {

        jdbcTemplate.execute(con -> {
            PreparedStatement ps = con.prepareStatement(
                    insertQuery,
                    Statement.RETURN_GENERATED_KEYS
            );

            for (Song song : songs) {
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
                ps.addBatch();
            }

            ps.executeBatch();
            return ps;
        }, (PreparedStatementCallback<Void>) ps -> {
            ResultSet rs = ps.getGeneratedKeys();
            List<Long> generatedIds = new ArrayList<>();
            while (rs.next()) {
                generatedIds.add(rs.getLong(1));
            }

            for (int i = 0; i < songs.size(); i++) {
                songs.get(i).setId(generatedIds.get(i));
            }
            return null;
        });

        return songs;
    }
}
