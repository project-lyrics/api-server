package com.projectlyrics.server.domain.user.api;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.projectlyrics.server.domain.user.dto.request.UserUpdateRequest;
import com.projectlyrics.server.domain.user.dto.response.UserGetResponse;
import com.projectlyrics.server.domain.user.dto.response.UserProfileResponse;
import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.support.RestDocsTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.ArrayList;
import java.util.List;

class UserControllerTest extends RestDocsTest {

    @Test
    void 사용자의_프로필을_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        User user = UserFixture.create();
        UserProfileResponse result = UserProfileResponse.from(user);

        given(userQueryService.getById(any()))
                .willReturn(result);

        // when, then
        mockMvc.perform(get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getUserProfileDocument());
    }

    private RestDocumentationResultHandler getUserProfileDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("User API")
                        .summary("사용자 프로필 조회 API")
                        .requestHeaders(getAuthorizationHeader())
                        .responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("사용자 Id"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING)
                                        .description("사용자 닉네임"),
                                fieldWithPath("profileCharacterType").type(JsonFieldType.STRING)
                                        .description("사용자 프로필 이미지 타입" + getEnumValuesAsString(ProfileCharacter.class)),
                                fieldWithPath("gender").type(JsonFieldType.STRING)
                                        .description("사용자 성별" + getEnumValuesAsString(Gender.class)),
                                fieldWithPath("birthYear").type(JsonFieldType.NUMBER)
                                        .description("사용자 출생연도"),
                                fieldWithPath("feedbackId").type(JsonFieldType.STRING)
                                        .description("사용자 피드백 UUID"),
                                fieldWithPath("authProvider").type(JsonFieldType.STRING)
                                        .description("인증 플랫폼" + getEnumValuesAsString(AuthProvider.class))
                        )
                        .responseSchema(Schema.schema("User Profile Response"))
                        .build())
        );
    }

    @Test
    void 블락한_사용자들의_리스트를_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        List<UserGetResponse> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = UserFixture.create();
            result.add(UserGetResponse.from(user));
        }

        given(userQueryService.getAllBlocks(any()))
                .willReturn(result);

        // when, then
        mockMvc.perform(get("/api/v1/users/blocks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getUserBlockListDocument());
    }

    private RestDocumentationResultHandler getUserBlockListDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("User API")
                        .summary("사용자 차단 리스트 조회 API")
                        .requestHeaders(getAuthorizationHeader())
                        .responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER)
                                        .description("사용자 Id"),
                                fieldWithPath("[].nickname").type(JsonFieldType.STRING)
                                        .description("사용자 닉네임"),
                                fieldWithPath("[].profileCharacterType").type(JsonFieldType.STRING)
                                        .description("사용자 프로필 이미지 타입" + getEnumValuesAsString(ProfileCharacter.class))
                        )
                        .responseSchema(Schema.schema("User Block List Response"))
                        .build())
        );
    }

    @Test
    void 사용자의_프로필을_수정하면_200_응답을_해야_한다() throws Exception {
        // given
        UserUpdateRequest request =  new UserUpdateRequest(
                "검정치마",
                ProfileCharacter.BRAIDED_HAIR,
                Gender.FEMALE,
                2020
        );

        // when, then
        mockMvc.perform(patch("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(getUserUpdateDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getUserUpdateDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("User API")
                        .summary("사용자 프로필 수정 API")
                        .requestHeaders(getAuthorizationHeader())
                        .requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING)
                                        .description("사용자 닉네임"),
                                fieldWithPath("profileCharacter").type(JsonFieldType.STRING)
                                        .description("사용자 프로필 이미지 타입" + getEnumValuesAsString(ProfileCharacter.class)),
                                fieldWithPath("gender").type(JsonFieldType.STRING)
                                        .description("사용자 성별" + getEnumValuesAsString(Gender.class)),
                                fieldWithPath("birthYear").type(JsonFieldType.NUMBER)
                                        .description("사용자 출생연도")
                        )
                        .requestSchema(Schema.schema("Update User Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Update User Response"))
                        .build())
        );
    }

    @Test
    void 처음_방문한_사용자인지의_여부와_200응답을_해야_한다() throws Exception {
        // given
        User user = UserFixture.create();
        UserProfileResponse result = UserProfileResponse.from(user);

        given(userQueryService.getById(any()))
                .willReturn(result);

        // when, then
        mockMvc.perform(get("/api/v1/users/first-time")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getUserFirstTimeDocument());
    }

    private RestDocumentationResultHandler getUserFirstTimeDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("User API")
                        .summary("사용자 첫방문 여부 조회 API")
                        .requestHeaders(getAuthorizationHeader())
                        .responseFields(
                                fieldWithPath("isFirst").type(JsonFieldType.BOOLEAN)
                                        .description("사용자 첫방문 여부")
                        )
                        .responseSchema(Schema.schema("User First Time Response"))
                        .build())
        );
    }
}