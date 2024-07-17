package com.projectlyrics.server.domain.auth.entity;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkEnum;
import static com.projectlyrics.server.domain.common.util.DomainUtils.checkString;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "auths")
@Entity
public class SocialInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private AuthProvider authProvider;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String socialId;

    public static SocialInfo of(AuthProvider authProvider, Role role, String socialId) {
        checkEnum(authProvider);
        checkEnum(role);
        checkString(socialId);

        return new SocialInfo(
                authProvider,
                role,
                socialId
        );
    }

    private SocialInfo(AuthProvider authProvider, Role role, String socialId) {
        this.authProvider = authProvider;
        this.role = role;
        this.socialId = socialId;
    }
}
