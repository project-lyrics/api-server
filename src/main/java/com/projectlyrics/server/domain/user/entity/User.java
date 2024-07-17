package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Embedded
    private UserMetaInfo info;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TermsAgreements> termsAgreements;

    private User(SocialInfo socialInfo, String nickname, ProfileCharacter profileCharacter, Gender gender, int birthYear, List<TermsAgreements> termsAgreements) {
        checkNull(socialInfo);
        checkNull(termsAgreements);
        this.socialInfo = socialInfo;
        this.nickname = new Username(nickname);
        this.profileCharacter = profileCharacter;
        this.info = new UserMetaInfo(gender, birthYear);
        this.termsAgreements = termsAgreements;
        termsAgreements.forEach(terms -> terms.setUser(this));
    }

    public static User createUser(AuthSocialInfo socialInfo, AuthSignUpRequest request) {
        List<TermsAgreements> termsList = request.terms().stream()
                .map(termsInput -> new TermsAgreements(termsInput.agree(), termsInput.title(), termsInput.agreement()))
                .toList();
        return new User(
                SocialInfo.of(socialInfo.authProvider(), Role.USER, socialInfo.socialId()),
                request.nickname(),
                request.profileCharacter(),
                request.gender(),
                request.birthYear().getValue(),
                termsList
        );
    }

    public static User of(SocialInfo socialInfo, String nickname, ProfileCharacter profileCharacter, Gender gender, int birthYear, List<TermsAgreements> termsAgreements) {
        return new User(socialInfo, nickname, profileCharacter, gender, birthYear, termsAgreements);
    }
}
