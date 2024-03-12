package com.projectlyrics.server.domain.artist.entity;

import com.projectlyrics.server.domain.common.entity.EntityCommonField;
import com.projectlyrics.server.global.error_code.ErrorCode;
import com.projectlyrics.server.global.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "artists")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Artist {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = true)
  private String name;

  @Column(nullable = true)
  private String englishName;

  @Column(nullable = true)
  private String profileImageCdnLink;

  @Embedded
  private EntityCommonField commonField;

  @Builder
  private Artist(String name, String englishName, String profileImageCdnLink) {
    this.name = name;
    this.englishName = englishName;
    this.profileImageCdnLink = profileImageCdnLink;
    this.commonField = EntityCommonField.withDefaultValue();
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateEnglishName(String englishName) {
    this.englishName = englishName;
  }

  public void updateProfileImageCdnLink(String profileImageCdnLink) {
    if (!profileImageCdnLink.startsWith("https://")) {
      throw new BusinessException(ErrorCode.ARTIST_UPDATE_NOT_VALID);
    }

    this.profileImageCdnLink = profileImageCdnLink;
  }
}
