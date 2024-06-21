package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.entity.Auth;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "auth_id")
    private Auth auth;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private int birthYear;

    private TermsAgreements termsAgreements;

    @Builder
    public User(String email, Auth auth, String username, Gender gender, int birthYear, TermsAgreements termsAgreements) {
        this.email = email;
        this.auth = auth;
        this.username = username;
        this.gender = gender;
        this.birthYear = birthYear;
        this.termsAgreements = termsAgreements;
    }

    public static User from(AuthSocialInfo socialInfo, AuthSignUpRequest request) {
        return new User(
                socialInfo.email(),
                new Auth(socialInfo.socialId(), socialInfo.authProvider(), Role.USER),
                request.username(),
                request.gender(),
                request.birthYear().getValue(),
                new TermsAgreements(request.isAbove14(), request.termsOfService(), request.privacyPolicy())
        );
    }

    public static User of(
            final String socialId,
            final String email,
            final AuthProvider authProvider,
            final Role role
    ) {
        return User.builder()
                .auth(new Auth(socialId, authProvider, role))
                .email(email)
                .build();
    }
}
