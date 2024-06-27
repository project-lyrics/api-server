package com.projectlyrics.server.domain.auth.service.social;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class SocialServiceFactory {

    private final Map<AuthProvider, SocialService> socialServiceMap;

    public SocialServiceFactory(List<SocialService> socialServiceList) {
        socialServiceMap = new HashMap<>();

        socialServiceList.forEach(
                socialService -> socialServiceMap.put(socialService.getAuthProvider(), socialService)
        );
    }

    public SocialService getSocialServiceFrom(AuthProvider authProvider) {
        return socialServiceMap.get(authProvider);
    }
}
