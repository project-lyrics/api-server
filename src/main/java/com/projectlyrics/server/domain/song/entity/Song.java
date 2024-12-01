package com.projectlyrics.server.domain.song.entity;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.global.dev.cron.dto.Album;
import com.projectlyrics.server.global.dev.cron.dto.Track;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "songs")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Song extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String spotifyId;
    private String name;
    private LocalDate releaseDate;
    private String albumName;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Artist artist;

    @OneToMany(mappedBy = "song")
    private List<Note> notes = new ArrayList<>();

    public Song(
            Long id
    ) {
        this.id = id;
    }

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

    public Song(
            Album album,
            Track track,
            Artist artist
    ) {
        this.spotifyId = track.getId();
        this.name = track.getName();
        this.releaseDate = album.getDate();
        this.albumName = album.getName();
        this.imageUrl = album.getImages().getFirst().getUrl();
        this.artist = artist;
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

    public List<Note> getNotes() {
        return notes.stream()
                .filter(BaseEntity::isInUse)
                .toList();
    }
}
