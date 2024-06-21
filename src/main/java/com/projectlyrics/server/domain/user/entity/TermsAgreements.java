package com.projectlyrics.server.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermsAgreements {

    @Column(nullable = false)
    boolean above14;

    @Column(nullable = false)
    boolean termsOfService;

    @Column(nullable = false)
    boolean privacyPolicy;

    public TermsAgreements(boolean above14, boolean termsOfService, boolean privacyPolicy) {
        this.above14 = above14;
        this.termsOfService = termsOfService;
        this.privacyPolicy = privacyPolicy;
    }
}
