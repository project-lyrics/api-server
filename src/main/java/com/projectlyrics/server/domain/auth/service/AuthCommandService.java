package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.authentication.jwt.JwtClaim;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtExtractor;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtProvider;
import com.projectlyrics.server.domain.auth.domain.Auth;
import com.projectlyrics.server.domain.auth.domain.AuthGetSocialInfo;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import com.projectlyrics.server.domain.auth.authentication.jwt.AuthToken;
import com.projectlyrics.server.domain.auth.exception.AlreadyExistsUserException;
import com.projectlyrics.server.domain.auth.repository.AuthRepository;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.entity.UserCreate;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthCommandService {

    private final AuthQueryService authQueryService;
    private final AuthRepository authRepository;
    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;
    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;

    public AuthTokenResponse signUp(AuthSignUpRequest request) {
        SocialInfo socialInfo = authQueryService.getSocialInfo(AuthGetSocialInfo.from(request));
        checkIfAlreadyExists(socialInfo);

        User user = userCommandRepository.save(User.create(UserCreate.of(socialInfo, request)));
        AuthToken authToken = jwtProvider.issueTokens(user.getId(), user.getNickname().getValue());

        authRepository.save(Auth.create(user.getSocialInfo().getSocialId(), authToken.refreshToken()));
        return AuthTokenResponse.from(authToken);
    }

    private void checkIfAlreadyExists(SocialInfo socialInfo) {
        if (userQueryRepository.existsBySocialInfo(socialInfo)) {
            throw new AlreadyExistsUserException();
        }
    }

    public AuthTokenResponse signIn(AuthSignInRequest request) {
        SocialInfo socialInfo = authQueryService.getSocialInfo(AuthGetSocialInfo.from(request));
        User user = userQueryRepository.findBySocialIdAndAuthProvider(socialInfo.getSocialId(), socialInfo.getAuthProvider())
                .orElseThrow(UserNotFoundException::new);
        AuthToken authToken = jwtProvider.issueTokens(user.getId(), user.getNickname().getValue());

        authRepository.save(Auth.create(user.getSocialInfo().getSocialId(), authToken.refreshToken()));
        return AuthTokenResponse.from(authToken);
    }

    public AuthTokenResponse reissueAccessToken(String refreshToken) {
        JwtClaim jwtClaim = jwtExtractor.parseJwtClaim(refreshToken);
        AuthToken authToken = jwtProvider.issueTokens(jwtClaim);

        return AuthTokenResponse.from(authToken);
    }
}
