package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.auth.entity.Auth;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.TermsAgreements;
import com.projectlyrics.server.domain.user.entity.User;

import java.util.List;

public class UserFixture {

    private String email = "test@test.com";
    private Auth auth = Auth.of(AuthProvider.KAKAO, Role.USER, "socialId");
    private String nickname = "nickname";
    private ProfileCharacter profileCharacter = ProfileCharacter.POOP_HAIR;
    private Gender gender = Gender.MALE;
    private int birthYear = 1999;
    private List<TermsAgreements> termsAgreements = List.of(new TermsAgreements(true, "title", "agreement"));

    private UserFixture() {}

    public static User create() {
        return User.of(
                Auth.of(AuthProvider.KAKAO, Role.USER, "socialId"),
                "nickname",
                ProfileCharacter.POOP_HAIR,
                Gender.MALE,
                1999,
                List.of(new TermsAgreements(true, "title", "agreement")
                ));
    }

    public static UserFixture builder() {
        return new UserFixture();
    }

    public User build() {
        return User.of(auth, nickname, profileCharacter, gender, birthYear, termsAgreements);
    }

    public UserFixture auth(Auth auth) {
        this.auth = auth;
        return this;
    }

    public UserFixture kakao() {
        this.auth = Auth.of(AuthProvider.KAKAO, Role.USER, "socialId");
        return this;
    }

    public UserFixture apple() {
        this.auth = Auth.of(AuthProvider.APPLE, Role.USER, "socialId");
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
