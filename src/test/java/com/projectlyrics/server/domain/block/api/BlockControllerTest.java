package com.projectlyrics.server.domain.block.api;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.support.RestDocsTest;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

class BlockControllerTest extends RestDocsTest {
    @Test
    void 차단을_저장하면_200응답을_반환한다() throws Exception {
        // when, then
        mockMvc.perform(post("/api/v1/blocks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .queryParam("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getBlockCreateDocument());
    }

    private RestDocumentationResultHandler getBlockCreateDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getCursorBasePagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 1);
        queryParams[pagingQueryParameters.length] = parameterWithName("userId")
                .type(SimpleType.NUMBER)
                .description("차단할 사용자 Id");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Block API")
                        .summary("차단 등록 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(queryParams)
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .requestSchema(Schema.schema("Create Block Request"))
                        .build())
        );
    }

    @Test
    void 북마크를_삭제하면_200응답을_반환한다() throws Exception {
        // when, then
        mockMvc.perform(delete("/api/v1/blocks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .queryParam("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getBookmarkDeleteDocument());
    }

    private RestDocumentationResultHandler getBookmarkDeleteDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getCursorBasePagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 1);
        queryParams[pagingQueryParameters.length] = parameterWithName("userId")
                .type(SimpleType.NUMBER)
                .description("차단을 풀 사용자 Id");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Block API")
                        .summary("차단 삭제 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(queryParams)
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .requestSchema(Schema.schema("Delete Block Request"))
                        .build())
        );
    }
}
