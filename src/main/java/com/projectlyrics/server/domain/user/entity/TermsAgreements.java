package com.projectlyrics.server.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermsAgreements {

    @Column(nullable = false)
    private boolean agree;

    @Column(nullable = false)
    private String agreement;


    public TermsAgreements(boolean agree, String agreement) {
        this.agree = agree;
        this.agreement = agreement;
    }
}
