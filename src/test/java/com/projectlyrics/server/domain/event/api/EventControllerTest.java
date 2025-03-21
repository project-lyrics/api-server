package com.projectlyrics.server.domain.event.api;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.dto.response.EventGetResponse;
import com.projectlyrics.server.support.RestDocsTest;
import com.projectlyrics.server.support.fixture.EventFixture;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

class EventControllerTest extends RestDocsTest {

    private static final String deviceIdHeader = "Device-Id";
    private static final String deviceIdValue = "device_id";

    @Test
    void 이벤트를_저장하면_200응답을_해야_한다() throws Exception {
        // given
        EventCreateRequest request = new EventCreateRequest(
                "imageUrl",
                "redirectUrl",
                "button",
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
                                fieldWithPath("buttonText").type(JsonFieldType.STRING)
                                        .description("버튼 문구"),
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
                        .header(deviceIdHeader, deviceIdValue)
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
                        .requestHeaders(getAuthorizationHeader().optional())
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

    @Test
    void 사용자가_이벤트_리스트를_조회하면_데이터와_200응답을_해야_한다() throws Exception{
        // given
        List<EventGetResponse> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Event event = EventFixture.create();
            data.add(EventGetResponse.of(event));
        }

        CursorBasePaginatedResponse<EventGetResponse> response = new CursorBasePaginatedResponse<>(
                data.get(data.size() - 1).getId(),
                true,
                data
        );
        given(eventQueryService.getAllExceptRefusedByUser(any(Long.class), any(Long.class), anyInt()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/events")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .header(deviceIdHeader, deviceIdValue)
                        .param("cursor", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getAllExceptRefusedDocument());
    }

    private RestDocumentationResultHandler getAllExceptRefusedDocument() {

        RestDocumentationResultHandler document = restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Event API")
                        .summary("진행 중인 모든 이벤트 리스트 조회 API (사용자가 거부한 이벤트 제외)")
                        .requestHeaders(getAuthorizationHeader().optional())
                        .responseFields(
                                fieldWithPath("refusalPeriod").type(JsonFieldType.NUMBER)
                                        .description("거절 기간(1:하루동안 보지 않기/7:일주일간 보지 않기)"),
                                fieldWithPath("event").type(JsonFieldType.OBJECT)
                                        .description("이벤트 페이지네이션 관련 데아터"),
                                fieldWithPath("event.nextCursor").type(JsonFieldType.NUMBER)
                                        .description("다음 cursor에 쓰일 값"),
                                fieldWithPath("event.hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 데이터 존재 여부"),
                                fieldWithPath("event.data").type(JsonFieldType.ARRAY)
                                        .description("데이터"),
                                fieldWithPath("event.data[].id").type(JsonFieldType.NUMBER)
                                        .description("이벤트 Id"),
                                fieldWithPath("event.data[].imageUrl").type(JsonFieldType.STRING)
                                        .description("이미지 url"),
                                fieldWithPath("event.data[].buttonText").type(JsonFieldType.STRING)
                                        .description("버튼 문구"),
                                fieldWithPath("event.data[].redirectUrl").type(JsonFieldType.STRING)
                                        .description("리다이렉트 url")
                        )
                        .responseSchema(Schema.schema("Event List Response"))
                        .build()
                )
        );
        return document;
    }
}