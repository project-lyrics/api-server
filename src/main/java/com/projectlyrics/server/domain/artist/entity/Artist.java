package com.projectlyrics.server.domain.artist.entity;

import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.common.util.DomainUtils;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "artists")
@Entity
public class Artist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String secondName;
    private String thirdName;
    private String spotifyId;
    private String imageUrl;
    private LocalDateTime deletedAt;
    private Long deletedBy;

    private Artist(
            Long id,
            String name,
            String secondName,
            String thirdName,
            String spotifyId,
            String imageUrl
    ) {
        this.id = id;
        this.name = name;
        this.secondName = secondName;
        this.thirdName = thirdName;
        this.spotifyId = spotifyId;
        this.imageUrl = imageUrl;
    }

    public static Artist withId(
            Long id,
            String name,
            String secondName,
            String thirdName,
            String spotifyId,
            String imageUrl
    ) {
        return new Artist(id, name, secondName, thirdName, spotifyId, imageUrl);
    }

    public static Artist create(ArtistCreate artistCreate) {
        return new Artist(
                artistCreate.id(),
                artistCreate.name(),
                artistCreate.secondName(),
                artistCreate.thirdName(),
                artistCreate.spotifyId(),
                artistCreate.imageUrl()
        );
    }

    public Artist update(ArtistUpdate artistUpdate) {
        updateIfNotBlank(artistUpdate.name(), this::updateName);
        updateIfNotBlank(artistUpdate.secondName(), this::updateSecondName);
        updateIfNotBlank(artistUpdate.thirdName(), this::updateThirdName);
        updateIfNotBlank(artistUpdate.imageUrl(), this::updateImageUrl);

        return this;
    }

    private void updateName(String name) {
        this.name = name;
    }

    private void updateSecondName(String name) {
        this.secondName = name;
    }

    private void updateThirdName(String name) {
        this.thirdName = name;
    }

    private void updateImageUrl(String imageUrl) {
        DomainUtils.checkUrl(imageUrl);
        this.imageUrl = imageUrl;
    }

    public void updateIfNotBlank(String value, Consumer<String> updater) {
        if (StringUtils.hasText(value)) {
            updater.accept(value);
        }
    }

    public void delete(Long deletedBy, LocalDateTime deletedAt) {
        this.deletedBy = deletedBy;
        this.deletedAt = deletedAt;
    }
}
