package com.projectlyrics.server.domain.event.api;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.support.RestDocsTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDate;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest extends RestDocsTest {

    @Test
    void 이벤트를_저장하면_200응답을_해야_한다() throws Exception {
        // given
        EventCreateRequest request = new EventCreateRequest(
                "imageUrl",
                "redirectUrl",
                LocalDate.now()
        );

        // when, then
        mockMvc.perform(post("/api/v1/events")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(getCreateEventDocument());
    }

    private RestDocumentationResultHandler getCreateEventDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Event API")
                        .summary("이벤트 등록 API")
                        .requestHeaders(getAuthorizationHeader())
                        .requestFields(
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                        .description("이미지 URL"),
                                fieldWithPath("redirectUrl").type(JsonFieldType.STRING)
                                        .description("리다이렉트 URL"),
                                fieldWithPath("dueDate").type(JsonFieldType.STRING)
                                        .description("이벤트 마감일")
                        )
                        .requestSchema(Schema.schema("Create Event Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Create Event Response"))
                        .build())
        );
    }

    @Test
    void 이벤트를_거부하면_200응답을_해야_한다() throws Exception {
        // when, then
        mockMvc.perform(post("/api/v1/events/refuse")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("eventId", "1"))
                .andExpect(status().isOk())
                .andDo(getRefuseEventDocument());
    }

    private RestDocumentationResultHandler getRefuseEventDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Event API")
                        .summary("이벤트 거부 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(parameterWithName("eventId").type(SimpleType.NUMBER)
                                .description("거부할 이벤트 ID")
                        )
                        .requestSchema(Schema.schema("Refuse Event Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Refuse Event Response"))
                        .build())
        );
    }
}