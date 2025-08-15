package com.projectlyrics.server.domain.artist.entity;

import jakarta.persistence.Id;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "artists")
public class ArtistMongo {
    @Id
    private final Long id;

    @Field("search_names")
    private final String searchNames;

    private ArtistMongo(Long id, String searchNames) {
        this.id = id;
        this.searchNames = searchNames;
    }

    public static ArtistMongo of(Artist artist) {
        String searchNames = Stream.of(
                        artist.getName(),
                        artist.getSecondName(),
                        artist.getThirdName())
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));
        return new ArtistMongo(artist.getId(), searchNames);
    }
}

