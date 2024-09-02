package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkEnum;
import static com.projectlyrics.server.domain.common.util.DomainUtils.checkNull;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private SocialInfo socialInfo;

    @Embedded
    private Username nickname;

    @Enumerated(EnumType.STRING)
    private ProfileCharacter profileCharacter;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Embedded
    private UserMetaInfo info;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TermsAgreements> termsAgreements;

    private String fcmToken;

    private User(
            Long id,
            SocialInfo socialInfo,
            String nickname,
            ProfileCharacter profileCharacter,
            Role role,
            Gender gender,
            Integer birthYear,
            List<TermsAgreements> termsAgreements,
            String fcmToken
    ) {
        checkNull(socialInfo);
        checkNull(termsAgreements);
        checkEnum(role);
        this.id = id;
        this.socialInfo = socialInfo;
        this.nickname = new Username(nickname);
        this.profileCharacter = profileCharacter;
        this.role = role;
        this.info = new UserMetaInfo(gender, birthYear);
        this.termsAgreements = termsAgreements;
        termsAgreements.forEach(terms -> terms.setUser(this));
        this.fcmToken = fcmToken;
    }

    private User(
            SocialInfo socialInfo,
            String nickname,
            ProfileCharacter profileCharacter,
            Role role,
            Gender gender,
            Integer birthYear,
            List<TermsAgreements> termsAgreements,
            String fcmToken
    ) {
        this(null, socialInfo, nickname, profileCharacter, role, gender, birthYear, termsAgreements, fcmToken);
    }

    public static User withId(
            Long id,
            SocialInfo socialInfo,
            String nickname,
            ProfileCharacter profileCharacter,
            Role role,
            Gender gender,
            Integer birthYear,
            List<TermsAgreements> termsAgreements,
            String fcmToken
    ) {
        return new User(id, socialInfo, nickname, profileCharacter, role, gender, birthYear, termsAgreements, fcmToken);
    }

    public static User create(UserCreate userCreate) {
        return new User(
                userCreate.socialInfo(),
                userCreate.nickname(),
                userCreate.profileCharacter(),
                userCreate.role(),
                userCreate.gender(),
                userCreate.birthYear(),
                userCreate.termsAgreements(),
                userCreate.fcmToken()
        );
    }
}