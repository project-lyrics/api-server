package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserCommandService {

    private final UserCommandRepository userCommandRepository;

    public User create(User user) {
        return userCommandRepository.save(user);
    }
}
