package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenReissueResponse;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.common.util.TokenUtils;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.service.UserCommandService;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthCommandService {

    private final AuthQueryService authQueryService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public AuthCommandService(
            AuthQueryService authQueryService,
            UserQueryService userQueryService,
            JwtTokenProvider jwtTokenProvider,
            UserCommandService userCommandService) {
        this.authQueryService = authQueryService;
        this.userQueryService = userQueryService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userCommandService = userCommandService;
    }

    public AuthTokenResponse signIn(AuthSignInRequest request) {
        AuthSocialInfo socialInfo = authQueryService.getAuthSocialInfo(
                request.socialAccessToken(),
                request.authProvider()
        );
        User user = userQueryService.getUserBySocialInfo(socialInfo.socialId(), request.authProvider());

        return AuthTokenResponse.of(
                jwtTokenProvider.issueTokens(user.getId())
        );
    }

    public AuthTokenReissueResponse reissueAccessToken(String refreshToken) {
        String extractedToken = TokenUtils.extractToken(refreshToken);

        Long userId = jwtTokenProvider.getUserIdFromJwt(extractedToken);
        AuthToken authToken = jwtTokenProvider.issueTokens(userId);

        return AuthTokenReissueResponse.from(authToken);
    }

    public AuthTokenResponse signUp(AuthSignUpRequest request) {
        AuthSocialInfo socialInfo = authQueryService.getAuthSocialInfo(
                request.socialAccessToken(),
                request.authProvider()
        );

        User user = userCommandService.create(User.createUser(socialInfo, request));

        return AuthTokenResponse.of(
                jwtTokenProvider.issueTokens(user.getId())
        );
    }
}
