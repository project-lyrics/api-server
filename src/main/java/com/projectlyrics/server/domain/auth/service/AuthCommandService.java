package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.authentication.jwt.JwtClaim;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtExtractor;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtProvider;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenReissueResponse;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import com.projectlyrics.server.domain.auth.authentication.jwt.AuthToken;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.service.UserCommandService;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthCommandService {

    private final AuthQueryService authQueryService;
    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public AuthCommandService(
            AuthQueryService authQueryService,
            UserQueryService userQueryService,
            JwtProvider jwtProvider,
            JwtExtractor jwtExtractor, 
            UserCommandService userCommandService
    ) {
        this.authQueryService = authQueryService;
        this.userQueryService = userQueryService;
        this.jwtProvider = jwtProvider;
        this.jwtExtractor = jwtExtractor;
        this.userCommandService = userCommandService;
    }

    public AuthTokenResponse signIn(AuthSignInRequest request) {
        AuthSocialInfo socialInfo = authQueryService.getAuthSocialInfo(
                request.socialAccessToken(),
                request.authProvider()
        );
        User user = userQueryService.getUserBySocialInfo(socialInfo.socialId(), request.authProvider());

        return AuthTokenResponse.of(
                jwtProvider.issueTokens(user.getId(), user.getNickname().getValue())
        );
    }

    public AuthTokenReissueResponse reissueAccessToken(String refreshToken) {
        JwtClaim jwtClaim = jwtExtractor.parseJwtClaim(refreshToken);
        AuthToken authToken = jwtProvider.issueTokens(jwtClaim);

        return AuthTokenReissueResponse.from(authToken);
    }

    public AuthTokenResponse signUp(AuthSignUpRequest request) {
        AuthSocialInfo socialInfo = authQueryService.getAuthSocialInfo(
                request.socialAccessToken(),
                request.authProvider()
        );

        User user = userCommandService.create(User.createUser(socialInfo, request));

        return AuthTokenResponse.of(
                jwtProvider.issueTokens(user.getId(), user.getNickname().getValue())
        );
    }
}
