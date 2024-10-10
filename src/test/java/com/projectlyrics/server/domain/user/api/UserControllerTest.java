package com.projectlyrics.server.domain.user.api;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.projectlyrics.server.domain.user.dto.response.UserProfileResponse;
import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.support.RestDocsTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                                fieldWithPath("feedbackId").type(JsonFieldType.STRING)
                                        .description("사용자 피드백 UUID"),
                                fieldWithPath("authProvider").type(JsonFieldType.STRING)
                                        .description("인증 플랫폼" + getEnumValuesAsString(AuthProvider.class))
                        )
                        .responseSchema(Schema.schema("User Profile Response"))
                        .build())
        );
    }
}