package com.projectlyrics.server.domain.user.repository;

import com.projectlyrics.server.domain.user.dto.response.UserGetResponse;
import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
import com.projectlyrics.server.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserQueryRepository {

    Optional<User> findBySocialIdAndAuthProvider(String socialId, AuthProvider authProvider);

    Optional<User> findById(Long id);

    Optional<User> findDeletedBySocialIdAndAuthProvider(String socialId, AuthProvider authProvider);

    List<User> findAll();

    List<UserGetResponse> findAllBlocked(Long id);

    boolean existsBySocialInfo(SocialInfo socialInfo);

    boolean existsBySocialInfoAndForceDelete(SocialInfo socialInfo);
}
