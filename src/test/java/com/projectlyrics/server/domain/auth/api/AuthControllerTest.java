package com.projectlyrics.server.domain.auth.api;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
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
import com.projectlyrics.server.support.RestDocsTest;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.Year;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends RestDocsTest {

    @Test
    void 로그인할_때_소셜_로그인_인증된_유저는_인증_토큰과_200응답을_받아야_한다() throws Exception {
        //given
        AuthSignInRequest request = new AuthSignInRequest("socialAccessToken", AuthProvider.KAKAO);
        AuthTokenResponse response = new AuthTokenResponse(accessToken, refreshToken);
        given(authCommandService.signIn(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(print())
                .andDo(getSignInDocument(true));
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getSignInDocument(false));
    }

    @Test
    void 로그인할_때_소셜_인증에_실패한_유저는_401응답을_받아야_한다() throws Exception {
        //given
        AuthSignInRequest request = new AuthSignInRequest("socialAccessToken", AuthProvider.KAKAO);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_SOCIAL_TOKEN);
        given(authCommandService.signIn(any()))
                .willThrow(FeignException.class);

        //when then
        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getSignInDocument(false));
    }

    private RestDocumentationResultHandler getSignInDocument(boolean successCase) {
        FieldDescriptor[] responseFields = successCase ? getSignInSuccessResponseField() : getErrorResponseFields();
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Auth API")
                        .summary("로그인 API")
                        .requestFields(
                                fieldWithPath("socialAccessToken").type(JsonFieldType.STRING)
                                        .description("소셜 인증 토큰"),
                                fieldWithPath("authProvider").type(JsonFieldType.STRING)
                                        .description("인증 플랫폼" + getEnumValuesAsString(AuthProvider.class))
                        )
                        .responseFields(responseFields)
                        .requestSchema(Schema.schema("SignIn Request"))
                        .responseSchema(Schema.schema(successCase ? "SignIn Response" : ERROR_RESPONSE_SCHEMA))
                        .build())
        );
    }

    private FieldDescriptor[] getSignInSuccessResponseField() {
        return new FieldDescriptor[]{
                fieldWithPath("accessToken").type(JsonFieldType.STRING)
                        .description("Access Token"),
                fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                        .description("Refresh Token")
        };
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getSignUpDocument(true));
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getSignUpDocument(false));
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
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_SOCIAL_TOKEN);
        given(authCommandService.signUp(any()))
                .willThrow(FeignException.class);

        //when then
        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getSignUpDocument(false));
    }

    private FieldDescriptor[] getSignUpSuccessResponseField() {
        return new FieldDescriptor[]{
                fieldWithPath("accessToken").type(JsonFieldType.STRING)
                        .description("Access Token"),
                fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                        .description("Refresh Token")
        };
    }

    private RestDocumentationResultHandler getSignUpDocument(boolean successCase) {
        FieldDescriptor[] responseFields = successCase ? getSignUpSuccessResponseField() : getErrorResponseFields();
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Auth API")
                        .summary("회원가입 API")
                        .requestFields(
                                fieldWithPath("socialAccessToken").type(JsonFieldType.STRING)
                                        .description("소셜 인증 토큰"),
                                fieldWithPath("authProvider").type(JsonFieldType.STRING)
                                        .description("인증 플랫폼" + getEnumValuesAsString(AuthProvider.class)),
                                fieldWithPath("nickname").type(JsonFieldType.STRING)
                                        .description("닉네임")
                                        .attributes(constraints(
                                                """
                                                        - 최소 1자
                                                        - 한/영/숫자 상관없이 최대 10자
                                                        - 이모티콘, 특수문자, 공백 사용 불가능
                                                        - 유저 간 중복 허용
                                                        """
                                        )),
                                fieldWithPath("profileCharacter").type(JsonFieldType.STRING)
                                        .description("프로필 이미지"),
                                fieldWithPath("gender").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("성별" + getEnumValuesAsString(Gender.class)),
                                fieldWithPath("birthYear").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("출생년도"),
                                fieldWithPath("terms").type(JsonFieldType.ARRAY)
                                        .description("약관"),
                                fieldWithPath("terms[].agree").type(JsonFieldType.BOOLEAN)
                                        .description("약관 동의"),
                                fieldWithPath("terms[].title").type(JsonFieldType.STRING)
                                        .description("약관 제목"),
                                fieldWithPath("terms[].agreement").type(JsonFieldType.STRING)
                                        .description("약관 내용 (노션 링크)")
                        )
                        .responseFields(responseFields)
                        .requestSchema(Schema.schema("SignUp Request"))
                        .responseSchema(Schema.schema(successCase ? "SignUp Response" : ERROR_RESPONSE_SCHEMA))
                        .build())
        );
    }
}
