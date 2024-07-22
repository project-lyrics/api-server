package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.user.entity.SocialInfo;
import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.Role;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.TermsAgreements;
import com.projectlyrics.server.domain.user.entity.User;

import java.util.List;

public class UserFixture extends BaseFixture {

    private Long id;
    private String email = "test@test.com";
    private SocialInfo socialInfo = SocialInfo.of(AuthProvider.KAKAO, "socialId");
    private String nickname = "nickname";
    private ProfileCharacter profileCharacter = ProfileCharacter.POOP_HAIR;
    private Gender gender = Gender.MALE;
    private int birthYear = 1999;
    private Role role = Role.USER;
    private List<TermsAgreements> termsAgreements = List.of(new TermsAgreements(true, "title", "agreement"));

    private UserFixture() {}

    public static User create() {
        return User.withId(
                getUniqueId(),
                SocialInfo.of(AuthProvider.KAKAO, "socialId"),
                "nickname",
                ProfileCharacter.POOP_HAIR,
                Role.USER,
                Gender.MALE,
                1999,
                List.of(new TermsAgreements(true, "title", "agreement")
                ));
    }

    public static UserFixture builder() {
        return new UserFixture();
    }

    public User build() {
        return User.withId(id, socialInfo, nickname, profileCharacter, role, gender, birthYear, termsAgreements);
    }

    public UserFixture role(Role role) {
        this.role = role;
        return this;
    }

    public UserFixture id(Long id) {
        this.id = id;
        return this;
    }

    public UserFixture auth(SocialInfo socialInfo) {
        this.socialInfo = socialInfo;
        return this;
    }

    public UserFixture kakao() {
        this.socialInfo = SocialInfo.of(AuthProvider.KAKAO, "socialId");
        return this;
    }

    public UserFixture apple() {
        this.socialInfo = SocialInfo.of(AuthProvider.APPLE, "socialId");
        return this;
    }

    public UserFixture profileCharacter(ProfileCharacter profileCharacter) {
        this.profileCharacter = profileCharacter;
        return this;
    }

    public UserFixture username(String username) {
        this.nickname = username;
        return this;
    }

    public UserFixture gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public UserFixture birth(int birthYear) {
        this.birthYear = birthYear;
        return this;
    }

    public UserFixture terms(TermsAgreements termsAgreements) {
        this.termsAgreements.add(termsAgreements);
        return this;
    }
}
