package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.user.controller.dto.response.UserProfileResponse;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;

    public UserProfileResponse getProfile(Long id) {
        return userQueryRepository.findProfile(id);
    }
}
