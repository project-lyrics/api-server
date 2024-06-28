package com.projectlyrics.server.domain.artist.entity;

import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.common.util.DomainUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Optional;
import java.util.function.Consumer;

import lombok.*;
import org.springframework.util.StringUtils;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkString;
import static com.projectlyrics.server.domain.common.util.DomainUtils.checkUrl;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "artists")
@Entity
public class Artist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String imageUrl;

    public static Artist of(String name, String imageUrl) {
        return new Artist(name, imageUrl);
    }

    public static Artist from(ArtistAddRequest dto) {
        return new Artist(
                dto.name(),
                dto.imageUrl()
        );
    }

    private Artist(String name, String imageUrl) {
        checkString(name);
        Optional.ofNullable(imageUrl)
                .ifPresent(DomainUtils::checkUrl);
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateImageUrl(String imageUrl) {
        DomainUtils.checkUrl(imageUrl);

        this.imageUrl = imageUrl;
    }

    public void updateIfNotBlank(String value, Consumer<String> updater) {
        if (StringUtils.hasText(value)) {
            updater.accept(value);
        }
    }
}
