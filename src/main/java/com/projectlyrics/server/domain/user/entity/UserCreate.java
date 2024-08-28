package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;

import java.util.List;
import java.util.Objects;

public record UserCreate(
        SocialInfo socialInfo,
        String nickname,
        ProfileCharacter profileCharacter,
        Gender gender,
        Integer birthYear,
        List<TermsAgreements> termsAgreements,
        String fcmToken,
        Role role
) {

    public static UserCreate of(SocialInfo socialInfo, AuthSignUpRequest request) {
        List<TermsAgreements> termsList = request.terms().stream()
                .map(termsInput -> new TermsAgreements(termsInput.agree(), termsInput.title(), termsInput.agreement()))
                .toList();

        Role role = request.isAdmin() ? Role.ADMIN : Role.USER;

        return new UserCreate(
                socialInfo,
                request.nickname(),
                request.profileCharacter(),
                request.gender(),
                Objects.nonNull(request.birthYear()) ? request.birthYear().getValue() : null,
                termsList,
                null,
                role
        );
    }
}
