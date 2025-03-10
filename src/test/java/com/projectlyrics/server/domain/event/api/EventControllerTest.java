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
                "popupImageUrl",
                "bannerImageUrl",
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
                                fieldWithPath("popupImageUrl").type(JsonFieldType.STRING)
                                        .description("팝업 이미지 url"),
                                fieldWithPath("bannerImageUrl").type(JsonFieldType.STRING)
                                        .description("배너 이미지 url"),
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

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Event API")
                        .summary("진행 중인 모든 이벤트 리스트 조회 API (사용자가 거부한 이벤트 제외)")
                        .requestHeaders(getAuthorizationHeader())
                        .responseFields(
                                fieldWithPath("nextCursor").type(JsonFieldType.NUMBER)
                                        .description("다음 cursor에 쓰일 값"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 데이터 존재 여부"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("이벤트 Id"),
                                fieldWithPath("data[].popupImageUrl").type(JsonFieldType.STRING)
                                        .description("팝업 이미지 url"),
                                fieldWithPath("data[].bannerImageUrl").type(JsonFieldType.STRING)
                                        .description("배너 이미지 url"),
                                fieldWithPath("data[].redirectUrl").type(JsonFieldType.STRING)
                                        .description("리다이렉트 url")
                        )
                        .responseSchema(Schema.schema("Event List Response"))
                        .build()
                )
        );
    }
}