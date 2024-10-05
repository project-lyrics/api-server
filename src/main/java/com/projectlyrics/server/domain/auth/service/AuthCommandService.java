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
import com.projectlyrics.server.domain.bookmark.repository.BookmarkCommandRepository;
import com.projectlyrics.server.domain.comment.repository.CommentCommandRepository;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistCommandRepository;
import com.projectlyrics.server.domain.like.repository.LikeCommandRepository;
import com.projectlyrics.server.domain.note.repository.NoteCommandRepository;
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
    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final BookmarkCommandRepository bookmarkCommandRepository;
    private final CommentCommandRepository commentCommandRepository;
    private final FavoriteArtistCommandRepository favoriteArtistCommandRepository;
    private final LikeCommandRepository likeCommandRepository;
    private final NoteCommandRepository noteCommandRepository;

    public AuthTokenResponse signUp(AuthSignUpRequest request) {
        SocialInfo socialInfo = authQueryService.getSocialInfo(AuthGetSocialInfo.from(request));
        checkIfAlreadyExists(socialInfo);

        User user = userCommandRepository.save(User.create(UserCreate.of(socialInfo, request)));

        AuthToken authToken = issueAndSaveToken(user);
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

    public AuthTokenResponse signIn(AuthSignInRequest request) {
        SocialInfo socialInfo = authQueryService.getSocialInfo(AuthGetSocialInfo.from(request));
        User user = userQueryRepository.findBySocialIdAndAuthProvider(socialInfo.getSocialId(), socialInfo.getAuthProvider())
                .orElseThrow(UserNotFoundException::new);

        AuthToken authToken = issueAndSaveToken(user);
        return AuthTokenResponse.of(authToken, user.getId());
    }

    public AuthTokenResponse reissueToken(String refreshToken) {
        Auth auth = authRepository.findByRefreshToken(refreshToken)
                .orElseThrow(AuthNotFoundException::new);
        User user = userQueryRepository.findBySocialIdAndAuthProvider(auth.getSocialId(), auth.getAuthProvider())
                .orElseThrow(UserNotFoundException::new);

        AuthToken authToken = issueAndSaveToken(user);
        return AuthTokenResponse.of(authToken, user.getId());
    }

    private AuthToken issueAndSaveToken(User user) {
        AuthToken authToken = jwtProvider.issueTokens(user.getId(), user.getNickname().getValue(), user.getRole());
        authRepository.save(Auth.create(user.getSocialInfo(), authToken.refreshToken()));

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
                .ifPresent(authRepository::delete);
        bookmarkCommandRepository.deleteAllByUserId(userId);
        commentCommandRepository.deleteAllByWriterId(userId);
        favoriteArtistCommandRepository.deleteAllByUserId(userId);
        likeCommandRepository.deleteAllByUserId(userId);
        noteCommandRepository.deleteAllByPublisherId(userId);
        userCommandRepository.deleteById(userId);
    }
}
