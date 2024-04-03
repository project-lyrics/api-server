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
public class ArtistEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = true, length = 200)
  private String name;

  @Column(nullable = true, length = 200)
  private String englishName;

  @Column(nullable = true, length = 2000)
  private String profileImageCdnLink;

  @Embedded
  private EntityCommonField commonField = EntityCommonField.withDefaultValue();

  public ArtistEntity(String name, String englishName, String profileImageCdnLink) {
    this.name = name;
    this.englishName = englishName;
    this.profileImageCdnLink = profileImageCdnLink;
    this.commonField = EntityCommonField.withDefaultValue();
  }
}
