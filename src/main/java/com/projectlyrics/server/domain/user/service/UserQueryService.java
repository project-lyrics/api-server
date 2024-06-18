package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.global.exception.NotFoundException;

import java.util.Optional;

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
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    public Optional<User> getUserBySocialInfo(String socialId, AuthProvider authProvider) {
        return userQueryRepository.findBySocialIdAndAuthProviderAndNotDeleted(socialId, authProvider);
    }
}
