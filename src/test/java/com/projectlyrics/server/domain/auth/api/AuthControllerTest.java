package com.projectlyrics.server.domain.auth.api;

import com.projectlyrics.server.common.ControllerTest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.exception.NotAgreeToTermsException;
import com.projectlyrics.server.domain.common.dto.ErrorResponse;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.Year;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends ControllerTest {

    @Test
    void 로그인할_때_소셜_로그인_인증된_유저는_인증_토큰과_200응답을_받아야_한다() throws Exception {
        //given
        AuthSignInRequest request = new AuthSignInRequest("socialAccessToken", AuthProvider.KAKAO);
        AuthTokenResponse response = new AuthTokenResponse("access token", "refresh token");
        given(authCommandService.signIn(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void 로그인할_때_소셜_액세스_토큰을_입력하지_않은_클라이언트는_() throws Exception {
        //given
        AuthSignInRequest request = new AuthSignInRequest(null, AuthProvider.KAKAO);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);

        //when then
        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALID_REQUEST_FIELD.getErrorCode()));
    }

    @Test
    void 로그인할_때_존재하지_않는_유저인_경우_404응답을_받아야_한다() throws Exception {
        //given
        AuthSignInRequest request = new AuthSignInRequest("socialAccessToken", AuthProvider.KAKAO);
        UserNotFoundException e = new UserNotFoundException();
        given(authCommandService.signIn(any()))
                .willThrow(e);
        ErrorResponse response = ErrorResponse.of(e.getErrorCode());

        //when then
        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void 로그인할_때_소셜_인증에_실패한_유저는_401응답을_받아야_한다() throws Exception {
        //given
        AuthSignInRequest request = new AuthSignInRequest("socialAccessToken", AuthProvider.KAKAO);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TOKEN);
        given(authCommandService.signIn(any()))
                .willThrow(FeignException.class);

        //when then
        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void 회원가입할_때_소셜_로그인_인증된_유저는_인증_토큰과_200응답을_받아야_한다() throws Exception {
        //given
        AuthSignUpRequest request = new AuthSignUpRequest(
                "socialAccessToken",
                AuthProvider.KAKAO,
                "nickname",
                ProfileCharacter.POOP_HAIR,
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
        );
        AuthTokenResponse response = new AuthTokenResponse("access token", "refresh token");
        given(authCommandService.signUp(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void 회원가입할_때_약관동의를_하지_않은_유저는_400응답을_받아야_한다() throws Exception {
        //given
        AuthSignUpRequest request = new AuthSignUpRequest(
                "socialAccessToken",
                AuthProvider.KAKAO,
                "nickname",
                ProfileCharacter.POOP_HAIR,
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(false, "title", "agreement"))
        );
        NotAgreeToTermsException e = new NotAgreeToTermsException();
        ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        given(authCommandService.signUp(any()))
                .willThrow(e);

        //when then
        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void 회원가입할_때_소셜_인증에_실패한_유저는_401응답을_받아야_한다() throws Exception {
        //given
        AuthSignUpRequest request = new AuthSignUpRequest(
                "socialAccessToken",
                AuthProvider.KAKAO,
                "nickname",
                ProfileCharacter.POOP_HAIR,
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
        );
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TOKEN);
        given(authCommandService.signUp(any()))
                .willThrow(FeignException.class);

        //when then
        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }
}
