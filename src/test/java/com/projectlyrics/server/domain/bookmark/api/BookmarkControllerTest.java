package com.projectlyrics.server.domain.bookmark.api;

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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookmarkControllerTest extends RestDocsTest {

    @Test
    void 북마크를_저장하면_200응답과_북마크_id를_반환한다() throws Exception {
        // when, then
        mockMvc.perform(post("/api/v1/bookmarks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .queryParam("noteId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getBookmarkCreateDocument());
    }

    private RestDocumentationResultHandler getBookmarkCreateDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getCursorBasePagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 1);
        queryParams[pagingQueryParameters.length] = parameterWithName("noteId")
                .type(SimpleType.NUMBER)
                .description("노트 Id");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Bookmark API")
                        .summary("북마크 등록 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(queryParams)
                        .responseFields(
                                fieldWithPath("noteId").type(JsonFieldType.NUMBER)
                                        .description("노트 Id")
                        )
                        .requestSchema(Schema.schema("Create Bookmark Request"))
                        .build())
        );
    }

    @Test
    void 북마크를_삭제하면_200응답과_북마크_id를_반환한다() throws Exception {
        // when, then
        mockMvc.perform(delete("/api/v1/bookmarks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .queryParam("noteId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getBookmarkDeleteDocument());
    }

    private RestDocumentationResultHandler getBookmarkDeleteDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getCursorBasePagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 1);
        queryParams[pagingQueryParameters.length] = parameterWithName("noteId")
                .type(SimpleType.NUMBER)
                .description("노트 Id");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Bookmark API")
                        .summary("북마크 삭제 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(queryParams)
                        .responseFields(
                                fieldWithPath("noteId").type(JsonFieldType.NUMBER)
                                        .description("노트 Id")
                        )
                        .requestSchema(Schema.schema("Delete Bookmark Request"))
                        .build())
        );
    }
}