package com.projectlyrics.server.domain.auth.entity;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "auths")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Auth {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(value = EnumType.STRING)
  private AuthProvider authProvider;

  @Column(nullable = false)
  private Long socialId;

  public Auth(Long socialId, AuthProvider authProvider) {
    this.socialId = socialId;
    this.authProvider = authProvider;
  }
}
