package com.projectlyrics.server.domain.notification.api;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.projectlyrics.server.domain.notification.api.dto.request.PublicNotificationCreateRequest;
import com.projectlyrics.server.support.RestDocsTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest extends RestDocsTest {

    @Test
    void 전체_알림을_등록하면_결과_상태값과_200응답을_해야_한다() throws Exception {
        // given
        PublicNotificationCreateRequest request = new PublicNotificationCreateRequest("content");

        // when, then
        mockMvc.perform(post("/api/v1/notifications/public")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(getPublicNotificationCreateDocument());
    }

    private RestDocumentationResultHandler getPublicNotificationCreateDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Notification API")
                        .summary("전체 알림 등록 API")
                        .requestHeaders(getAuthorizationHeader())
                        .requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("알림 내용")
                        )
                        .requestSchema(Schema.schema("Create Public Notification Request"))
                        .responseFields(
                                fieldWithPath("status").type(JsonFieldType.BOOLEAN)
                                        .description("요청 처리 상태")
                        )
                        .responseSchema(Schema.schema("Create Public Notification Response"))
                        .build())
        );
    }
}