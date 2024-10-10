package com.projectlyrics.server.domain.notification.api;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.notification.api.dto.request.PublicNotificationCreateRequest;
import com.projectlyrics.server.domain.notification.api.dto.response.NotificationGetResponse;
import com.projectlyrics.server.domain.notification.domain.NotificationType;
import com.projectlyrics.server.support.RestDocsTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
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

    @Test
    void 알림을_읽으면_상태값과_200응답을_해야_한다() throws Exception {
        // when, then
        mockMvc.perform(patch("/api/v1/notifications/{notificationId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(getNotificationCheckDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getNotificationCheckDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Notification API")
                        .summary("알림 확인 API")
                        .requestHeaders(getAuthorizationHeader())
                        .pathParameters(
                                parameterWithName("notificationId").type(SimpleType.NUMBER)
                                        .description("알림 ID")
                        )
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Check Notification Response"))
                        .build()
                )
        );
    }

    @Test
    void 알림을_최신순으로_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        List<NotificationGetResponse> data = new ArrayList<>();
        for (long id = 1; id <= 10; id++) {
            data.add(new NotificationGetResponse(
                    id,
                    NotificationType.PUBLIC,
                    "notification content",
                    LocalDateTime.now(),
                    false,
                    1L,
                    "note content",
                    "https://artist.image.com"
            ));
        }

        CursorBasePaginatedResponse<NotificationGetResponse> response = new CursorBasePaginatedResponse<>(
                data.get(data.size() - 1).getId(),
                true,
                data
        );

        given(notificationQueryService.getRecentNotifications(any(), any(), anyInt()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/notifications")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("cursor", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(getNotificationListOfUserDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getNotificationListOfUserDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Notification API")
                        .summary("사용자가 수신한 알림 리스트 최신순 조회")
                        .queryParameters(getCursorBasePagingQueryParameters())
                        .responseFields(
                                fieldWithPath("nextCursor").type(JsonFieldType.NUMBER)
                                        .description("다음 cursor에 쓰일 값"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 데이터 존재 여부"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("알림 Id"),
                                fieldWithPath("data[].type").type(JsonFieldType.STRING)
                                        .description("알림 타입 " + getEnumValuesAsString(NotificationType.class)),
                                fieldWithPath("data[].content").type(JsonFieldType.STRING)
                                        .description("알림 내용 (전체 알림의 경우)"),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
                                        .description("알림 생성 시간 (ISO-8601 표준)"),
                                fieldWithPath("data[].checked").type(JsonFieldType.BOOLEAN)
                                        .description("알림 확인 여부"),
                                fieldWithPath("data[].noteId").type(JsonFieldType.NUMBER)
                                        .description("알림과 관련한 노트 Id"),
                                fieldWithPath("data[].noteContent").type(JsonFieldType.STRING)
                                        .description("알림과 관련한 노트 내용"),
                                fieldWithPath("data[].artistImageUrl").type(JsonFieldType.STRING)
                                        .description("알림과 관련한 노트가 속한 레코드 아티스트 이미지")
                        )
                        .responseSchema(Schema.schema("Notification List Response"))
                        .build()
                )
        );
    }
}