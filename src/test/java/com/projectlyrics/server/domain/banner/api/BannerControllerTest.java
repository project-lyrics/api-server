package com.projectlyrics.server.domain.banner.api;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.projectlyrics.server.domain.banner.domain.Banner;
import com.projectlyrics.server.domain.banner.dto.request.BannerCreateRequest;
import com.projectlyrics.server.domain.banner.dto.response.BannerGetResponse;
import com.projectlyrics.server.support.RestDocsTest;
import com.projectlyrics.server.support.fixture.BannerFixture;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

public class BannerControllerTest extends RestDocsTest {

    @Test
    void 배너를_저장하면_200응답을_해야_한다() throws Exception {
        // given
        BannerCreateRequest request = new BannerCreateRequest(
                "imageUrl",
                "redirectUrl",
                LocalDate.now(),
                LocalDate.now()
        );

        // when, then
        mockMvc.perform(post("/api/v1/banners")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(getCreateBannerDocument());
    }

    private RestDocumentationResultHandler getCreateBannerDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Event API")
                        .summary("배너 등록 API")
                        .requestHeaders(getAuthorizationHeader())
                        .requestFields(
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                        .description("이미지 URL"),
                                fieldWithPath("redirectUrl").type(JsonFieldType.STRING)
                                        .description("리다이렉트 URL"),
                                fieldWithPath("startDate").type(JsonFieldType.STRING)
                                        .description("배너 노출 시작일")
                                        .optional(),
                                fieldWithPath("dueDate").type(JsonFieldType.STRING)
                                        .description("배너 노출 마감일")
                                        .optional()
                        )
                        .requestSchema(Schema.schema("Create Banner Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Create Banner Response"))
                        .build())
        );
    }

    @Test
    void 배너_리스트를_조회하면_데이터와_200응답을_해야_한다() throws Exception{
        // given
        List<BannerGetResponse> response = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Banner banner = BannerFixture.create();
            response.add(BannerGetResponse.of(banner));
        }

        given(bannerQueryService.getRecentBanners(anyInt()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/banners")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getAllExceptRefusedDocument());
    }

    private RestDocumentationResultHandler getAllExceptRefusedDocument() {

        RestDocumentationResultHandler document = restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Banner API")
                        .summary("최신 배너 리스트 조회 API")
                        .requestHeaders(getAuthorizationHeader().optional())
                        .responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER)
                                        .description("배너 Id"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING)
                                        .description("배너 url"),
                                fieldWithPath("[].redirectUrl").type(JsonFieldType.STRING)
                                        .description("리다이렉트 url")
                        )
                        .responseSchema(Schema.schema("Banner List Response"))
                        .build()
                )
        );
        return document;
    }
}
