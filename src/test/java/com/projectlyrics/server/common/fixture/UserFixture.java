package com.projectlyrics.server.common.fixture;

import com.projectlyrics.server.domain.auth.entity.Auth;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.TermsAgreements;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.entity.Username;

import java.util.List;

public class UserFixture {

    private String email = "test@test.com";
    private Auth auth = Auth.of(AuthProvider.KAKAO, Role.USER, "socialId");
    private String username = "username";
    private Gender gender = Gender.MALE;
    private int birthYear = 1999;
    private List<TermsAgreements> termsAgreements = List.of(new TermsAgreements(true, "title", "agreement"));

    private UserFixture() {}

    public static User create() {
        return User.of(
                "test@test.com",
                Auth.of(AuthProvider.KAKAO, Role.USER, "socialId"),
                "username",
                Gender.MALE,
                1999,
                List.of(new TermsAgreements(true, "title", "agreement")
                ));
    }

    public static UserFixture builder() {
        return new UserFixture();
    }

    public User build() {
        return User.of(email, auth, username, gender, birthYear, termsAgreements);
    }

    public UserFixture email(String email) {
        this.email = email;
        return this;
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

    public UserFixture username(String username) {
        this.username = username;
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
