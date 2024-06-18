package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.dto.request.AuthUserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenReissueResponse;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;
import com.projectlyrics.server.domain.auth.service.dto.AuthUserSignUpResult;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.dto.response.AuthLoginResponse;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.domain.common.util.TokenUtils;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.service.UserCommandService;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import com.projectlyrics.server.global.exception.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthCommandService {

    private final String adminSecret;
    private final AuthQueryService authQueryService;
    private final UserQueryService userQueryService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserCommandService userCommandService;

    public AuthCommandService(
            @Value("${auth.admin.secret}") String adminSecret,
            AuthQueryService authQueryService,
            UserQueryService userQueryService,
            JwtTokenProvider jwtTokenProvider,
            UserCommandService userCommandService) {
        this.adminSecret = adminSecret;
        this.authQueryService = authQueryService;
        this.userQueryService = userQueryService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userCommandService = userCommandService;
    }

    public AuthLoginResponse signIn(String socialAccessToken, AuthUserLoginRequest request) {
        AuthSocialInfo authSocialInfo = getAuthSocialInfo(socialAccessToken, request.authProvider());
        AuthUserSignUpResult userSignUpResult = getSignUpResult(authSocialInfo);

        return AuthLoginResponse.of(
                request.role(),
                jwtTokenProvider.issueTokens(userSignUpResult.user().getId()),
                userSignUpResult.isRegistered()
        );
    }

    private User signUp(AuthSocialInfo authSocialInfo) {
        return userCommandService.create(authSocialInfo.toEntity(Role.USER));
    }

    private AuthSocialInfo getAuthSocialInfo(String socialAccessToken, AuthProvider authProvider) {
        return authQueryService.getAuthSocialInfo(socialAccessToken, authProvider);
    }

    private AuthUserSignUpResult getSignUpResult(AuthSocialInfo authSocialInfo) {
        return userQueryService.getUserBySocialInfo(authSocialInfo.socialId(), authSocialInfo.authProvider())
                .map(user -> new AuthUserSignUpResult(user, true))
                .orElseGet(() -> {
                    User user = signUp(authSocialInfo);
                    return new AuthUserSignUpResult(user, false);
                });
    }

    public AuthTokenReissueResponse reissueAccessToken(String refreshToken) {
        String extractedToken = TokenUtils.extractToken(refreshToken);

        Long userId = jwtTokenProvider.getUserIdFromJwt(extractedToken);
        AuthToken authToken = jwtTokenProvider.issueTokens(userId);

        return AuthTokenReissueResponse.from(authToken);
    }

    public void validateAdminSecret(String secret) {
        if (!adminSecret.equals(secret)) {
            throw new AuthException(ErrorCode.INVALID_KEY);
        }
    }
}
