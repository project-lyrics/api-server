package com.projectlyrics.server.domain.song.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "songs")
public class SongMongo {
    @Id
    @Getter
    private final Long id;

    @Field("name")
    private final String name;

    private SongMongo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SongMongo of(Song song) {
        return new SongMongo(song.getId(), song.getName());
    }
}
