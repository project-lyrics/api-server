package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.user.dto.request.UserUpdateRequest;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {

    private final UserQueryRepository userQueryRepository;

    public void update(UserUpdateRequest request, Long id) {
        User user = userQueryRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        user.update(request);
    }
}
