package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.auth.exception.NotAgreeToTermsException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkString;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermsAgreements {

    @Column(nullable = false)
    private boolean agree;

    @Column(nullable = false)
    private String agreement;


    public TermsAgreements(boolean agree, String agreement) {
        if (!agree) {
            throw new NotAgreeToTermsException();
        }
        checkString(agreement);
        this.agree = agree;
        this.agreement = agreement;
    }
}
