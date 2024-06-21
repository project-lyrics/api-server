package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenReissueResponse;
import com.projectlyrics.server.domain.auth.exception.InvalidAdminKeyException;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.dto.response.AuthLoginResponse;
import com.projectlyrics.server.domain.common.util.TokenUtils;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.service.UserCommandService;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthCommandService {

    private final String adminSecret;
    private final AuthQueryService authQueryService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserQueryService userQueryService;
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

    public AuthLoginResponse signIn(AuthSignInRequest request) {
        User user = userQueryService.getUserBySocialInfo(request.socialAccessToken(), request.authProvider());

        return AuthLoginResponse.of(
                jwtTokenProvider.issueTokens(user.getId())
        );
    }

    public AuthTokenReissueResponse reissueAccessToken(String refreshToken) {
        String extractedToken = TokenUtils.extractToken(refreshToken);

        Long userId = jwtTokenProvider.getUserIdFromJwt(extractedToken);
        AuthToken authToken = jwtTokenProvider.issueTokens(userId);

        return AuthTokenReissueResponse.from(authToken);
    }

    public void validateAdminSecret(String secret) {
        if (!adminSecret.equals(secret)) {
            throw new InvalidAdminKeyException();
        }
    }
}
