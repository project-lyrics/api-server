package com.projectlyrics.server.domain.auth.service;

import com.projectlyrics.server.domain.auth.authentication.jwt.AuthToken;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtProvider;
import com.projectlyrics.server.domain.auth.domain.Auth;
import com.projectlyrics.server.domain.auth.domain.AuthGetSocialInfo;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import com.projectlyrics.server.domain.auth.exception.AlreadyExistsUserException;
import com.projectlyrics.server.domain.auth.exception.AuthNotFoundException;
import com.projectlyrics.server.domain.auth.exception.ForcedWithdrawalUserException;
import com.projectlyrics.server.domain.auth.repository.AuthRepository;
import com.projectlyrics.server.domain.block.repository.BlockCommandRepository;
import com.projectlyrics.server.domain.bookmark.repository.BookmarkCommandRepository;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistCommandRepository;
import com.projectlyrics.server.domain.like.repository.LikeCommandRepository;
import com.projectlyrics.server.domain.notification.repository.NotificationCommandRepository;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.entity.UserCreate;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import java.time.Clock;
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
    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final BookmarkCommandRepository bookmarkCommandRepository;
    private final FavoriteArtistCommandRepository favoriteArtistCommandRepository;
    private final LikeCommandRepository likeCommandRepository;
    private final NotificationCommandRepository notificationCommandRepository;
    private final BlockCommandRepository blockCommandRepository;

    public AuthTokenResponse signUp(AuthSignUpRequest request, String deviceId) {
        SocialInfo socialInfo = authQueryService.getSocialInfo(AuthGetSocialInfo.from(request));
        checkIfAlreadyExists(socialInfo);

        User user = userCommandRepository.save(User.create(UserCreate.of(socialInfo, request)));

        AuthToken authToken = issueAndSaveToken(user, deviceId);
        return AuthTokenResponse.of(authToken, user.getId());
    }

    private void checkIfAlreadyExists(SocialInfo socialInfo) {
        if (userQueryRepository.existsBySocialInfo(socialInfo)) {
            throw new AlreadyExistsUserException();
        }
        else if (userQueryRepository.existsBySocialInfoAndForceDelete(socialInfo)) {
            throw new ForcedWithdrawalUserException();
        }
    }

    public AuthTokenResponse signIn(AuthSignInRequest request, String deviceId) {
        SocialInfo socialInfo = authQueryService.getSocialInfo(AuthGetSocialInfo.from(request));
        User user = userQueryRepository.findBySocialIdAndAuthProvider(socialInfo.getSocialId(), socialInfo.getAuthProvider())
                .orElseThrow(UserNotFoundException::new);

        AuthToken authToken = issueAndSaveToken(user, deviceId);
        return AuthTokenResponse.of(authToken, user.getId());
    }

    public AuthTokenResponse reissueToken(String refreshToken) {
        Auth auth = authRepository.findByRefreshToken(refreshToken)
                .orElseThrow(AuthNotFoundException::new);
        User user = userQueryRepository.findBySocialIdAndAuthProvider(auth.getSocialId(), auth.getAuthProvider())
                .orElseThrow(UserNotFoundException::new);

        AuthToken authToken = issueAndSaveToken(user, auth.getDeviceId());
        return AuthTokenResponse.of(authToken, user.getId());
    }

    private AuthToken issueAndSaveToken(User user, String deviceId) {
        AuthToken authToken = jwtProvider.issueTokens(user.getId(), user.getNickname().getValue(), user.getRole());
        authRepository.save(Auth.create(user.getSocialInfo(), authToken.refreshToken(), deviceId));

        return authToken;
    }

    public void signOut(Long userId) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        authRepository.findById(user.getSocialInfo().getSocialId())
                .ifPresentOrElse(
                        authRepository::delete,
                        () -> { throw new AuthNotFoundException(); }
                );
    }

    public void delete(Long userId) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        authRepository.findById(user.getSocialInfo().getSocialId())
                .ifPresentOrElse(
                authRepository::delete,
                () -> { throw new AuthNotFoundException(); }
        );
        blockCommandRepository.deleteAllByBlockedId(userId);
        blockCommandRepository.deleteAllByBlockerId(userId);
        notificationCommandRepository.deleteAllByReceiverId(userId);
        notificationCommandRepository.deleteAllBySenderId(userId);
        bookmarkCommandRepository.deleteAllByUserId(userId);
        favoriteArtistCommandRepository.deleteAllByUserId(userId);
        likeCommandRepository.deleteAllByUserId(userId);

        user.withdraw();
    }

    public void forcedWithdrawal(User user) {
        authRepository.findById(user.getSocialInfo().getSocialId())
                .ifPresent(authRepository::delete);
        blockCommandRepository.deleteAllByBlockedId(user.getId());
        blockCommandRepository.deleteAllByBlockerId(user.getId());
        notificationCommandRepository.deleteAllByReceiverId(user.getId());
        notificationCommandRepository.deleteAllBySenderId(user.getId());
        bookmarkCommandRepository.deleteAllByUserId(user.getId());
        favoriteArtistCommandRepository.deleteAllByUserId(user.getId());
        likeCommandRepository.deleteAllByUserId(user.getId());

        user.forcedWithdrawal(Clock.systemDefaultZone());
    }
}
