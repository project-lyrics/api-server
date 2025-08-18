package com.projectlyrics.server.domain.song.entity;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "songs")
public class SongMongo {
    @Id
    private final Long id;

    @Field("artist_id")
    private final Long artistId;

    @Field("name")
    private final String name;

    public SongMongo(Long id, Long artistId, String name) {
        this.id = id;
        this.artistId = artistId;
        this.name = name;
    }
}
