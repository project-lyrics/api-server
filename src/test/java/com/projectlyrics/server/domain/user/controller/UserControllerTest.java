package com.projectlyrics.server.domain.user.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.projectlyrics.server.domain.user.controller.dto.response.UserProfileResponse;
import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.UserMetaInfo;
import com.projectlyrics.server.support.RestDocsTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.UUID;

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
        UserProfileResponse response = new UserProfileResponse(
                1L,
                "조휴일",
                ProfileCharacter.BRAIDED_HAIR,
                AuthProvider.KAKAO,
                UUID.randomUUID().toString(),
                new UserMetaInfo(Gender.FEMALE, 1999)
        );

        given(userQueryService.getProfile(any()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/users/profile")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(getUserProfileDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getUserProfileDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("User API")
                        .summary("사용자 프로필 조회")
                        .requestHeaders(getAuthorizationHeader())
                        .responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("사용자 id"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING)
                                        .description("사용자 닉네임"),
                                fieldWithPath("profileCharacter").type(JsonFieldType.STRING)
                                        .description("사용자 프로필 캐릭터 " + getEnumValuesAsString(ProfileCharacter.class)),
                                fieldWithPath("authProvider").type(JsonFieldType.STRING)
                                        .description("사용자 가입 방법 " + getEnumValuesAsString(AuthProvider.class)),
                                fieldWithPath("feedbackId").type(JsonFieldType.STRING)
                                        .description("사용자 피드백 전용 UUID"),
                                fieldWithPath("metaInfo.gender").type(JsonFieldType.STRING)
                                        .description("사용자 성별"),
                                fieldWithPath("metaInfo.birthYear").type(JsonFieldType.NUMBER)
                                        .description("사용자 생년")
                        )
                        .responseSchema(Schema.schema("User Profile Response"))
                        .build()
                )
        );
    }
}