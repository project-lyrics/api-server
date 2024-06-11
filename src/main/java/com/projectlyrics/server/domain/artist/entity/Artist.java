package com.projectlyrics.server.domain.artist.entity;

import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.global.exception.FeelinException;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
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

  @Builder
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
    if (!profileImageCdnLink.startsWith("https://")) {
      throw new FeelinException(ErrorCode.ARTIST_UPDATE_NOT_VALID);
    }

    this.profileImageCdnLink = profileImageCdnLink;
  }

  public void updateIfNotBlank(String value, Consumer<String> updater) {
    if (StringUtils.hasText(value)) {
      updater.accept(value);
    }
  }
}
