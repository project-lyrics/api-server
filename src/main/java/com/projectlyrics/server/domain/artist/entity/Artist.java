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

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String englishName;

    @Column(nullable = true)
    private String profileImageCdnLink;

    public static Artist from(ArtistAddRequest dto) {
        checkString(dto.name());
        checkString(dto.englishName());
        checkUrl(dto.profileImageCdnLink());

        return new Artist(
                dto.name(),
                dto.englishName(),
                dto.profileImageCdnLink()
        );
    }

    private Artist(String name, String englishName, String profileImageCdnLink) {
        this.name = name;
        this.englishName = englishName;
        this.profileImageCdnLink = profileImageCdnLink;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public void updateProfileImageCdnLink(String profileImageCdnLink) {
        DomainUtils.checkUrl(profileImageCdnLink);

        this.profileImageCdnLink = profileImageCdnLink;
    }

    public void updateIfNotBlank(String value, Consumer<String> updater) {
        if (StringUtils.hasText(value)) {
            updater.accept(value);
        }
    }
}
