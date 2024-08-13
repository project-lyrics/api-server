package com.projectlyrics.server.domain.like.api;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.support.RestDocsTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Arrays;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LikeControllerTest extends RestDocsTest {

    @Test
    void 좋아요를_저장하면_200응답과_좋아요_개수를_반환한다() throws Exception {
        // given
        given(likeQueryService.countLikesOfNote(anyLong()))
                .willReturn(1L);

        // when, then
        mockMvc.perform(post("/api/v1/likes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .queryParam("noteId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getCreateLikeDocument());
    }

    private RestDocumentationResultHandler getCreateLikeDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getPagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 1);
        queryParams[pagingQueryParameters.length] = parameterWithName("noteId")
                .type(SimpleType.NUMBER)
                .description("노트 Id");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Like API")
                        .summary("좋아요 등록 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(queryParams)
                        .responseFields(
                                fieldWithPath("likesCount").type(JsonFieldType.NUMBER)
                                        .description("좋아요 개수"),
                                fieldWithPath("noteId").type(JsonFieldType.NUMBER)
                                        .description("노트 Id")
                        )
                        .requestSchema(Schema.schema("Create Like Request"))
                        .build())
        );
    }

    @Test
    void 좋아요를_삭제하면_200응답과_좋아요_개수를_반환한다() throws Exception {
        // when, then
        mockMvc.perform(delete("/api/v1/likes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .queryParam("noteId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getDeleteLikeDocument());
    }

    private RestDocumentationResultHandler getDeleteLikeDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getPagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 1);
        queryParams[pagingQueryParameters.length] = parameterWithName("noteId")
                .type(SimpleType.NUMBER)
                .description("노트 Id");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Like API")
                        .summary("좋아요 삭제 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(queryParams)
                        .responseFields(
                                fieldWithPath("likesCount").type(JsonFieldType.NUMBER)
                                        .description("좋아요 개수"),
                                fieldWithPath("noteId").type(JsonFieldType.NUMBER)
                                        .description("노트 Id")
                        )
                        .requestSchema(Schema.schema("Delete Like Request"))
                        .build())
        );
    }
}