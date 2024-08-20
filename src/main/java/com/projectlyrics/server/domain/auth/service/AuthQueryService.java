package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.domain.AuthGetSocialInfo;
import com.projectlyrics.server.domain.auth.service.social.SocialService;
import com.projectlyrics.server.domain.auth.service.social.SocialServiceFactory;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthQueryService {

    private final SocialServiceFactory socialServiceFactory;

    public SocialInfo getSocialInfo(AuthGetSocialInfo getSocialInfo) {
        SocialService socialService = socialServiceFactory.getSocialServiceFrom(getSocialInfo.authProvider());

        return socialService.getSocialData(getSocialInfo.socialAccessToken());
    }
}
