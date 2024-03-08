package com.projectlyrics.server.domain.artist.entity;

import com.projectlyrics.server.domain.common.entity.EntityCommonField;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "artists")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Artist {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "artist_id")
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
}
