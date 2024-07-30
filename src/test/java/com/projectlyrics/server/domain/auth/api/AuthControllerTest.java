package com.projectlyrics.server.domain.auth.api;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.request.TokenReissueRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import com.projectlyrics.server.domain.auth.exception.AlreadyExistsUserException;
import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.auth.exception.InvalidTokenException;
import com.projectlyrics.server.domain.auth.exception.NotAgreeToTermsException;
import com.projectlyrics.server.domain.auth.exception.TokenExpiredException;
import com.projectlyrics.server.domain.common.dto.ErrorResponse;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.support.RestDocsTest;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.Year;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends RestDocsTest {

    @Test
    void 로그인할_때_소셜_로그인_인증된_유저는_인증_토큰과_200응답을_해야_한다() throws Exception {
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
    void 로그인할_때_존재하지_않는_유저인_경우_404응답을_해야_한다() throws Exception {
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
    void 로그인할_때_소셜_인증에_실패한_유저는_401응답을_해야_한다() throws Exception {
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
        FieldDescriptor[] responseFields = successCase ? getTokenResponseField() : getErrorResponseFields();
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

    @Test
    void 회원가입할_때_소셜_로그인_인증된_유저는_인증_토큰과_200응답을_해야_한다() throws Exception {
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
        AuthTokenResponse response = new AuthTokenResponse(accessToken, refreshToken);
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
    void 회원가입할_때_약관동의를_하지_않은_유저는_400응답을_해야_한다() throws Exception {
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
    void 회원가입할_때_이미_있는_유저인_경우_400응답을_해야_한다() throws Exception {
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
        AlreadyExistsUserException e = new AlreadyExistsUserException();
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
    void 회원가입할_때_소셜_인증에_실패한_유저는_401응답을_해야_한다() throws Exception {
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

    private RestDocumentationResultHandler getSignUpDocument(boolean successCase) {
        FieldDescriptor[] responseFields = successCase ? getTokenResponseField() : getErrorResponseFields();
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
                                        .description(
                                                """
                                                        닉네임
                                                        - 최소 1자
                                                        - 한/영/숫자 상관없이 최대 10자
                                                        - 이모티콘, 특수문자, 공백 사용 불가능
                                                        - 유저 간 중복 허용
                                                        """
                                        ),
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

    @Test
    void 토큰을_재발급에_성공하면_토큰과_200응답을_해야_한다() throws Exception {
        //given
        TokenReissueRequest request = new TokenReissueRequest(refreshToken);
        AuthTokenResponse response = new AuthTokenResponse(accessToken, refreshToken);
        given(authCommandService.reissueAccessToken(any()))
                .willReturn(response);

        //when true
        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getReissueTokenDocument(true));
    }

    @Test
    void refresh_token이_만료되었을_경우_400응답을_해야_한다() throws Exception {
        //given
        TokenReissueRequest request = new TokenReissueRequest(refreshToken);
        ErrorResponse response = ErrorResponse.of(ErrorCode.TOKEN_EXPIRED);
        given(authCommandService.reissueAccessToken(any()))
                .willThrow(new TokenExpiredException());

        //when true
        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getReissueTokenDocument(false));
    }

    @Test
    void 올바르지_않은_토큰일_경우_400응답을_해야_한다() throws Exception {
        //given
        TokenReissueRequest request = new TokenReissueRequest(refreshToken);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TOKEN);
        given(authCommandService.reissueAccessToken(any()))
                .willThrow(new InvalidTokenException());

        //when true
        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getReissueTokenDocument(false));
    }

    private RestDocumentationResultHandler getReissueTokenDocument(boolean successCase) {
        FieldDescriptor[] responseFields = successCase ? getTokenResponseField() : getErrorResponseFields();
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Auth API")
                        .summary("토큰 재발급 API")
                        .requestFields(
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                                        .description("Refresh Token")
                        )
                        .responseFields(responseFields)
                        .requestSchema(Schema.schema("Reissue Token Request"))
                        .responseSchema(Schema.schema(successCase ? "Reissue Token Response" : ERROR_RESPONSE_SCHEMA))
                        .build())
        );
    }

    private FieldDescriptor[] getTokenResponseField() {
        return new FieldDescriptor[]{
                fieldWithPath("accessToken").type(JsonFieldType.STRING)
                        .description("Access Token"),
                fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                        .description("Refresh Token")
        };
    }

    @Test
    void 유효한_토큰이면_200응답을_해야_한다() throws Exception {
        //when then
        mockMvc.perform(get("/api/v1/auth/validate-token")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                                resource(ResourceSnippetParameters.builder()
                                        .tag("Auth API")
                                        .summary("토큰 검증 API")
                                        .requestHeaders(getAuthorizationHeader())
                                        .responseFields(
                                                fieldWithPath("status").type(SimpleType.BOOLEAN)
                                        )
                                        .responseSchema(Schema.schema("Token Validate Response"))
                                        .build())
                        )
                );
    }

    @Test
    void 만료된_토큰이면_400응답을_해야_한다() throws Exception {
        //given
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsIm5pY2tuYW1lIjoidGVzdDEiLCJ0b2tlblR5cGUiOiJhY2Nlc3NUb2tlbiIsImlhdCI6MTcyMTEzNTI2MCwiZXhwIjoxNzIxMTM1MjYwfQ.Xj-lRRIWkYj_7JlfLl0hcjEfgABrnL7s8M2aBCdN71U";

        //when then
        mockMvc.perform(get("/api/v1/auth/validate-token")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken))
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document(
                                resource(ResourceSnippetParameters.builder()
                                        .tag("Auth API")
                                        .summary("토큰 검증 API")
                                        .requestHeaders(getAuthorizationHeader())
                                        .responseSchema(Schema.schema(ERROR_RESPONSE_SCHEMA))
                                        .build())
                        )
                );
    }

    @Test
    void 유효하지_않은_토큰이면_401응답을_해야_한다() throws Exception {
        //given

        //when then
        mockMvc.perform(get("/api/v1/auth/validate-token")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + "invalidToken"))
                .andExpect(status().isUnauthorized())
                .andDo(restDocs.document(
                                resource(ResourceSnippetParameters.builder()
                                        .tag("Auth API")
                                        .summary("토큰 검증 API")
                                        .requestHeaders(getAuthorizationHeader())
                                        .responseSchema(Schema.schema(ERROR_RESPONSE_SCHEMA))
                                        .build())
                        )
                );
    }
}
