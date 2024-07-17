package com.projectlyrics.server.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkEnum;
import static com.projectlyrics.server.domain.common.util.DomainUtils.checkString;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialInfo {

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
