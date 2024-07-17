package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;

    public User getUserById(long userId) {
        return userQueryRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public User getUserBySocialInfo(String socialId, AuthProvider authProvider) {
        return userQueryRepository.findBySocialIdAndAuthProviderAndNotDeleted(socialId, authProvider)
                .orElseThrow(UserNotFoundException::new);
    }

    public boolean existsBySocialInfo(SocialInfo socialInfo) {
        return userQueryRepository.existsBySocialInfo(socialInfo);
    }
}
