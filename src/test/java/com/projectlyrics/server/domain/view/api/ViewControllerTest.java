package com.projectlyrics.server.domain.view.api;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.support.RestDocsTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

public class ViewControllerTest extends RestDocsTest {
    private static final String deviceIdKey = "Device-Id";
    private static final String deviceIdValue = "device_id";

    @Test
    void 조회수를_저장하면_200응답을_해야_한다() throws Exception {
        // given

        // when, then
        mockMvc.perform(post("/api/v1/views/{noteId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .header(deviceIdKey, deviceIdValue)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getCreateViewDocument());
    }

    private RestDocumentationResultHandler getCreateViewDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("View API")
                        .summary("조회 생성 API")
                        .requestHeaders(getAuthorizationHeader())
                        .pathParameters(
                                parameterWithName("noteId").type(SimpleType.NUMBER)
                                        .description("노트 ID")
                        )
                        .requestSchema(Schema.schema("Create View Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Create View Response"))
                        .build())
        );
    }
}
