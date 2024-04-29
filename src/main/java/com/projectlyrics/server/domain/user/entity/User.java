package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.auth.entity.Auth;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.common.entity.EntityCommonField;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
  private String email;

  @Embedded
  private EntityCommonField commonField;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "auth_id")
  private Auth auth;

  @Builder
  public User(Long socialId, String email, AuthProvider authProvider) {
    this.auth = new Auth(socialId, authProvider);
    this.email = email;
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
