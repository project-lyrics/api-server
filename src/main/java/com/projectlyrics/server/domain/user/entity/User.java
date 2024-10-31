package com.projectlyrics.server.domain.user.entity;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkEnum;
import static com.projectlyrics.server.domain.common.util.DomainUtils.checkNull;

import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.common.entity.enumerate.EntityStatusEnum;
import com.projectlyrics.server.domain.user.dto.request.UserUpdateRequest;
import com.projectlyrics.server.domain.user.exception.FailedToUpdateProfileException;
import com.projectlyrics.server.domain.user.exception.UnchangedUsernameException;
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
import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String feedbackId;

    private User(
            Long id,
            SocialInfo socialInfo,
            String nickname,
            ProfileCharacter profileCharacter,
            Role role,
            Gender gender,
            Integer birthYear,
            List<TermsAgreements> termsAgreements
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
        this.feedbackId = UUID.randomUUID().toString();
        this.setStatus(EntityStatusEnum.YET);
    }

    private User(
            SocialInfo socialInfo,
            String nickname,
            ProfileCharacter profileCharacter,
            Role role,
            Gender gender,
            Integer birthYear,
            List<TermsAgreements> termsAgreements
    ) {
        this(null, socialInfo, nickname, profileCharacter, role, gender, birthYear, termsAgreements);
    }

    public static User withId(
            Long id,
            SocialInfo socialInfo,
            String nickname,
            ProfileCharacter profileCharacter,
            Role role,
            Gender gender,
            Integer birthYear,
            List<TermsAgreements> termsAgreements
    ) {
        return new User(id, socialInfo, nickname, profileCharacter, role, gender, birthYear, termsAgreements);
    }

    public static User create(UserCreate userCreate) {
        return new User(
                userCreate.socialInfo(),
                userCreate.nickname(),
                userCreate.profileCharacter(),
                userCreate.role(),
                userCreate.gender(),
                userCreate.birthYear(),
                userCreate.termsAgreements()
        );
    }

    public void withdraw() {
        nickname = null;
        profileCharacter = null;
        info = null;
        termsAgreements.forEach(TermsAgreements::delete);
        delete(id, Clock.systemDefaultZone());
    }

    public void forcedWithdrawal(Clock clock) {
        nickname = null;
        profileCharacter = null;
        info = null;
        termsAgreements.forEach(TermsAgreements::delete);
        forcedDelete(Clock.systemDefaultZone());
    }

    public void update(UserUpdateRequest request) {
        if (Objects.isNull(request.nickname())
                && Objects.isNull(request.profileCharacter())
                && Objects.isNull(request.gender())
                && Objects.isNull(request.birthYear())
        ) {
            throw new FailedToUpdateProfileException();
        }

        if (Objects.nonNull(request.nickname()) && !request.nickname().isEmpty()) {
            if (nickname.equals(request.nickname())) {
                throw new UnchangedUsernameException();
            }
            nickname = new Username(request.nickname());
        }

        if (Objects.nonNull(request.profileCharacter())) {
            profileCharacter = request.profileCharacter();
        }

        if (Objects.nonNull(request.gender())) {
            info = new UserMetaInfo(request.gender(), info == null ? null : info.getBirthYear());
        }

        if (Objects.nonNull(request.birthYear())) {
            info = new UserMetaInfo(info == null ? null : info.getGender(), request.birthYear());
        }
    }
}