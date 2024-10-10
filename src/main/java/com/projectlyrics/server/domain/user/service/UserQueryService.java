package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.user.dto.response.UserProfileResponse;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;

    public UserProfileResponse getById(Long id) {
        return UserProfileResponse.from(userQueryRepository.findById(id)
                        .orElseThrow(UserNotFoundException::new));
    }
}
