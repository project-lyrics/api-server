package com.projectlyrics.server.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtExtractor;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtProvider;
import com.projectlyrics.server.domain.auth.domain.Auth;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import com.projectlyrics.server.domain.auth.exception.AlreadyExistsUserException;
import com.projectlyrics.server.domain.auth.exception.ForcedWithdrawalUserException;
import com.projectlyrics.server.domain.auth.exception.NotAgreeToTermsException;
import com.projectlyrics.server.domain.auth.repository.AuthRepository;
import com.projectlyrics.server.domain.auth.service.social.apple.AppleSocialService;
import com.projectlyrics.server.domain.auth.service.social.apple.dto.AppleUserInfo;
import com.projectlyrics.server.domain.auth.service.social.kakao.KakaoSocialDataApiClient;
import com.projectlyrics.server.domain.auth.service.social.kakao.dto.KakaoUserInfo;
import com.projectlyrics.server.domain.common.entity.enumerate.EntityStatusEnum;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.entity.NoteType;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.projectlyrics.server.domain.note.service.NoteCommandService;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import feign.FeignException;
import java.time.Clock;
import java.time.Year;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

public class AuthCommandServiceIntegrationTest extends IntegrationTest {

    @Autowired
    AuthCommandService sut;

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    AuthQueryService authQueryService;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    JwtExtractor jwtExtractor;

    @Autowired
    AuthRepository authRepository;

    @Autowired
    NoteCommandService noteCommandService;

    @Autowired
    NoteQueryRepository noteQueryRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongCommandRepository songCommandRepository;

    @SpyBean
    KakaoSocialDataApiClient kakaoSocialDataApiClient;

    @SpyBean
    AppleSocialService appleSocialService;

    private User user;
    private AuthSignUpRequest request;
    private String deviceId;

    @BeforeEach
    void setUp() {
        user = UserFixture.create();
        request = new AuthSignUpRequest(
                "socialAccessToken",
                AuthProvider.KAKAO,
                "nickname",
                ProfileCharacter.POOP_HAIR,
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement")),
                false
        );
        deviceId = "device-id";
    }

    @Test
    void 회원가입_해야_한다() throws Exception {
        // given
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        // when
        AuthTokenResponse response = sut.signUp(request, deviceId);

        // then
        User signedUpUser = userQueryRepository.findBySocialIdAndAuthProvider(user.getSocialInfo().getSocialId(), AuthProvider.KAKAO).get();
        Long userId = jwtExtractor.parseJwtClaim(response.accessToken()).id();

        assertThat(userId).isEqualTo(signedUpUser.getId());
        assertThat(signedUpUser.getStatus()).isEqualTo(EntityStatusEnum.YET);
    }

    @Test
    void 이미_있는_유저인_경우_회원가입에_실패_해야_한다() throws Exception {
        // given
        userCommandRepository.save(user);
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        // when, then
        assertThatThrownBy(() -> sut.signUp(request, deviceId))
                .isInstanceOf(AlreadyExistsUserException.class);
    }

    @Test
    void 강제_탈퇴_이력이_있는_유저인_경우_회원가입에_실패_해야_한다() throws Exception {
        // given
        user.forcedWithdrawal(Clock.systemDefaultZone());
        userCommandRepository.save(user);
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        // when, then
        assertThatThrownBy(() -> sut.signUp(request, deviceId))
                .isInstanceOf(ForcedWithdrawalUserException.class);
    }


    @Test
    void 회원가입할_때_약관에_동의하지_않은_경우_예외가_발생해야_한다() throws Exception {
        // given
        AuthSignUpRequest unAgreedRequest = new AuthSignUpRequest(
                "socialAccessToken",
                AuthProvider.KAKAO,
                "nickname",
                ProfileCharacter.POOP_HAIR,
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(false, "title", "agreement")),
                false
        );
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        // when, then
        assertThatThrownBy(() -> sut.signUp(unAgreedRequest, deviceId))
                .isInstanceOf(NotAgreeToTermsException.class);
    }

    @Test
    void 소셜_인증에_실패한_경우_회원가입에_실패해야_한다() throws Exception {
        // when, then
        assertThatThrownBy(() -> sut.signUp(request, deviceId))
                .isInstanceOf(FeignException.class);
    }

    @Test
    void 회원가입시_리프레시_토큰_정보가_저장되어야_한다() {
        // given
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        // when
        AuthTokenResponse response = sut.signUp(request, deviceId);
        Auth auth = authRepository.findByRefreshToken(response.refreshToken()).get();

        // then
        assertThat(auth.getSocialId()).isEqualTo(user.getSocialInfo().getSocialId());
    }

    @Test
    void 카카오_계정으로_로그인_해야_한다() throws Exception {
        // given
        User savedUser = userCommandRepository.save(UserFixture.builder().kakao().build());
        doReturn(new KakaoUserInfo(savedUser.getSocialInfo().getSocialId()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        // when
        AuthTokenResponse response = sut.signIn(new AuthSignInRequest("accessToken", AuthProvider.KAKAO), deviceId);

        // then
        Long userId = jwtExtractor.parseJwtClaim(response.accessToken()).id();
        assertThat(userId).isEqualTo(savedUser.getId());
    }

    @Test
    void 애플_계정으로_로그인_해야_한다() throws Exception {
        // given
        User savedUser = userCommandRepository.save(UserFixture.builder().apple().build());
        doReturn(SocialInfo.from(new AppleUserInfo(savedUser.getSocialInfo().getSocialId())))
                .when(appleSocialService).getSocialData(any());

        // when
        AuthTokenResponse response = sut.signIn(new AuthSignInRequest("accessToken", AuthProvider.APPLE), deviceId);

        // then
        Long userId = jwtExtractor.parseJwtClaim(response.accessToken()).id();
        assertThat(userId).isEqualTo(savedUser.getId());
    }

    @Test
    void 없는_유저인_경우_로그인에_실패해야_한다() throws Exception {
        // given
        doReturn(new KakaoUserInfo("socialId"))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        // when, then
        assertThatThrownBy(() -> sut.signIn(new AuthSignInRequest("accessToken", AuthProvider.KAKAO), deviceId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 소셜_인증에_실패한_경우_로그인에_실패해야_한다() throws Exception {
        // given
        String socialAccessToken = "accessToken";

        // when, then
        assertThatThrownBy(() -> sut.signIn(new AuthSignInRequest(socialAccessToken, AuthProvider.KAKAO), deviceId))
                .isInstanceOf(FeignException.class);
    }

    @Test
    void 리프레쉬_토큰으로부터_토큰을_재발급할_수_있어야_한다() {
        // given
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());
        AuthTokenResponse signUpResponse = sut.signUp(request, deviceId);

        // when
        AuthTokenResponse reissuedToken = sut.reissueToken(signUpResponse.refreshToken());

        // then
        Long userId = jwtExtractor.parseJwtClaim(reissuedToken.accessToken()).id();
        User signedUpUser = userQueryRepository.findBySocialIdAndAuthProvider(user.getSocialInfo().getSocialId(), AuthProvider.KAKAO).get();

        assertThat(userId).isEqualTo(signedUpUser.getId());
    }

    @Test
    void 토큰이_재발급되고_저장되어야_한다() {
        // given
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());
        AuthTokenResponse signUpResponse = sut.signUp(request, deviceId);

        // when
        AuthTokenResponse reissuedToken = sut.reissueToken(signUpResponse.refreshToken());

        // then
        Auth auth = authRepository.findByRefreshToken(reissuedToken.refreshToken()).get();

        assertThat(auth.getSocialId()).isEqualTo(user.getSocialInfo().getSocialId());
    }

    @Test
    void 저장되어_있는_토큰을_삭제하여_로그아웃해야_한다() {
        // given
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());
        AuthTokenResponse signUpResponse = sut.signUp(request, deviceId);

        // when
        sut.signOut(jwtExtractor.parseJwtClaim(signUpResponse.accessToken()).id());

        // then
        assertThat(authRepository.findByRefreshToken(signUpResponse.refreshToken())).isEmpty();
    }

    @Test
    void 회원_탈퇴시_회원과_관련한_모든_정보가_함께_삭제되어야_한다() {
        // given
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());
        AuthTokenResponse signUpResponse = sut.signUp(request, deviceId);

        // when
        sut.delete(jwtExtractor.parseJwtClaim(signUpResponse.accessToken()).id());

        // then
        assertThat(userQueryRepository.findById(jwtExtractor.parseJwtClaim(signUpResponse.accessToken()).id())).isEmpty();
        assertThat(authRepository.findByRefreshToken(signUpResponse.refreshToken())).isEmpty();
    }

    @Test
    void 회원_탈퇴시_회원과_관련한_일부_정보가_남아야_한다() {
        // given
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());
        AuthTokenResponse signUpResponse = sut.signUp(request, deviceId);
        Note note = writeNote(jwtExtractor.parseJwtClaim(signUpResponse.accessToken()).id());

        // when
        sut.delete(jwtExtractor.parseJwtClaim(signUpResponse.accessToken()).id());

        // then
        User result = userQueryRepository.findDeletedBySocialIdAndAuthProvider(user.getSocialInfo().getSocialId(), user.getSocialInfo().getAuthProvider()).get();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getSocialInfo().getSocialId()).isEqualTo(user.getSocialInfo().getSocialId());
        assertThat(result.getSocialInfo().getAuthProvider()).isEqualTo(user.getSocialInfo().getAuthProvider());
        assertThat(result.getRole()).isEqualTo(user.getRole());
        assertThat(noteQueryRepository.findById(note.getId())).isNotNull();
    }

    private Note writeNote(Long userId) {
        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        Song song = songCommandRepository.save(SongFixture.create(artist));

        NoteCreateRequest noteCreateRequest = new NoteCreateRequest(
                "content",
                null,
                null,
                NoteStatus.PUBLISHED,
                NoteType.FREE,
                song.getId()
        );
        return noteCommandService.create(noteCreateRequest, userId);
    }
}
