package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.auth.exception.NotAgreeToTermsException;
import com.projectlyrics.server.domain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Clock;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkNull;
import static com.projectlyrics.server.domain.common.util.DomainUtils.checkString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermsAgreements extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean agree;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String agreement;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public TermsAgreements(boolean agree, String title, String agreement) {
        if (!agree) {
            throw new NotAgreeToTermsException();
        }

        checkString(title);
        this.agree = true;
        this.title = title;
        this.agreement = agreement;
    }

    public void setUser(User user) {
        checkNull(user);
        this.user = user;
    }

    public void delete() {
        delete(user.getId(), Clock.systemDefaultZone());
    }
}
