package com.projectlyrics.server.domain.song.entity;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "songs")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Song extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spotifyId;
    private String name;
    private LocalDate releaseDate;
    private String albumName;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Artist artist;

    private Song(
            Long id,
            String spotifyId,
            String name,
            LocalDate releaseDate,
            String albumName,
            String imageUrl,
            Artist artist
    ) {
        this.id = id;
        this.spotifyId = spotifyId;
        this.name = name;
        this.releaseDate = releaseDate;
        this.albumName = albumName;
        this.imageUrl = imageUrl;
        this.artist = artist;
    }

    private Song(
            String spotifyId,
            String name,
            LocalDate releaseDate,
            String albumName,
            String imageUrl,
            Artist artist
    ) {
        this(null, spotifyId, name, releaseDate, albumName, imageUrl, artist);
    }

    public static Song create(SongCreate songCreate) {
        return new Song(
                songCreate.id(),
                songCreate.spotifyId(),
                songCreate.name(),
                songCreate.releaseDate(),
                songCreate.albumName(),
                songCreate.imageUrl(),
                songCreate.artist()
        );
    }

    public static Song createWithId(Long id, SongCreate songCreate) {
        return new Song(
                id,
                songCreate.spotifyId(),
                songCreate.name(),
                songCreate.releaseDate(),
                songCreate.albumName(),
                songCreate.imageUrl(),
                songCreate.artist()
        );
    }
}
