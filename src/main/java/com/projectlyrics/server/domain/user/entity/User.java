package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.common.entity.EntityCommonField;
import com.projectlyrics.server.domain.auth.external.AuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "users")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = true)
  private Long socialId;

  @Column(nullable = true)
  private String email;

  @Enumerated(value = EnumType.STRING)
  private AuthProvider authProvider;

  @Embedded
  private EntityCommonField commonField;

  @Builder
  public User(Long socialId, String email, AuthProvider authProvider) {
    this.socialId = socialId;
    this.email = email;
    this.authProvider = authProvider;
    this.commonField = EntityCommonField.withDefaultValue();
  }

  public static User of(
      final Long socialId,
      final String email,
      final AuthProvider authProvider
  ) {
    return User.builder()
        .socialId(socialId)
        .email(email)
        .authProvider(authProvider)
        .build();
  }
}
