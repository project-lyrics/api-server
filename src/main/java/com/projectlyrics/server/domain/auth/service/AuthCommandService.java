package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.authentication.jwt.JwtClaim;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtExtractor;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtProvider;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import com.projectlyrics.server.domain.auth.authentication.jwt.AuthToken;
import com.projectlyrics.server.domain.auth.exception.AlreadyExistsUserException;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
import com.projectlyrics.server.domain.user.entity.User;
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
    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;
    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;

    public AuthTokenResponse signIn(AuthSignInRequest request) {
        AuthSocialInfo socialInfo = authQueryService.getAuthSocialInfo(
                request.socialAccessToken(),
                request.authProvider()
        );
        User user = userQueryRepository.findBySocialIdAndAuthProvider(socialInfo.socialId(), request.authProvider())
                .orElseThrow(UserNotFoundException::new);

        return AuthTokenResponse.of(
                jwtProvider.issueTokens(user.getId(), user.getNickname().getValue())
        );
    }

    public AuthTokenResponse reissueAccessToken(String refreshToken) {
        JwtClaim jwtClaim = jwtExtractor.parseJwtClaim(refreshToken);
        AuthToken authToken = jwtProvider.issueTokens(jwtClaim);

        return AuthTokenResponse.of(authToken);
    }

    public AuthTokenResponse signUp(AuthSignUpRequest request) {
        AuthSocialInfo socialInfo = authQueryService.getAuthSocialInfo(
                request.socialAccessToken(),
                request.authProvider()
        );

        if (userQueryRepository.existsBySocialInfo(SocialInfo.from(socialInfo))) {
            throw new AlreadyExistsUserException();
        }

        User user = userCommandRepository.save(User.createUser(socialInfo, request));

        return AuthTokenResponse.of(
                jwtProvider.issueTokens(user.getId(), user.getNickname().getValue())
        );
    }
}
