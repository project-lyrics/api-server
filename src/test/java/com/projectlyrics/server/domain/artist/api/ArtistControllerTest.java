package com.projectlyrics.server.domain.artist.api;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.artist.dto.request.ArtistCreateRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.common.dto.util.OffsetBasePaginatedResponse;
import com.projectlyrics.server.support.RestDocsTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

class ArtistControllerTest extends RestDocsTest {

    @Test
    void 아티스트를_추가하면_200응답을_해야_한다() throws Exception {
        // given
        ArtistCreateRequest request = new ArtistCreateRequest(
                "검정치마",
                "The Black Skirts",
                null,
                "6WeDO4GynFmK4OxwkBzMW8",
                "https://i.scdn.co/image/ab6761610000e5eb8609536d21beed6769d09d7f"
        );

        // when, then
        mockMvc.perform(post("/api/v1/artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(getArtistCreateDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getArtistCreateDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Artist API")
                        .summary("아티스트 등록 API")
                        .requestHeaders(getAuthorizationHeader())
                        .requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("아티스트 이름"),
                                fieldWithPath("secondName").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("아티스트 두번째 이름"),
                                fieldWithPath("thirdName").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("아티스트 세번째 이름"),
                                fieldWithPath("spotifyId").type(JsonFieldType.STRING)
                                        .description("스포티파이 아티스트 id"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                        .description("아티스트 이미지 url")
                        )
                        .requestSchema(Schema.schema("Artist Create Request"))
                        .build())
        );
    }

    @Test
    void 아티스트를_수정하면_200응답을_해야_한다() throws Exception {
        // given
        ArtistUpdateRequest request = new ArtistUpdateRequest(
                "초록불꽃소년단",
                "Green Flame Boys",
                "Green Flame Boyz",
                "https://i.scdn.co/image/ab6761610000e5eb8609536d21beed6769d09d7f"
        );

        // when, then
        mockMvc.perform(patch("/api/v1/artists/{artistId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(getArtistUpdateDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getArtistUpdateDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Artist API")
                        .summary("아티스트 수정 API")
                        .requestHeaders(getAuthorizationHeader())
                        .pathParameters(
                                parameterWithName("artistId").type(SimpleType.NUMBER)
                                        .description("아티스트 id")
                        )
                        .requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("아티스트 이름"),
                                fieldWithPath("secondName").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("아티스트 두번째 이름"),
                                fieldWithPath("thirdName").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("아티스트 세번째 이름"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                        .description("아티스트 이미지 url")
                        )
                        .requestSchema(Schema.schema("Artist Update Request"))
                        .build())
        );
    }

    @Test
    void 아티스트를_삭제하면_200응답을_해야_한다() throws Exception {
        // when, then
        mockMvc.perform(delete("/api/v1/artists/{artistId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(getArtistDeleteDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getArtistDeleteDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Artist API")
                        .summary("아티스트 삭제 API")
                        .requestHeaders(getAuthorizationHeader())
                        .pathParameters(
                                parameterWithName("artistId").type(SimpleType.NUMBER)
                                        .description("아티스트 id")
                        )
                        .requestSchema(Schema.schema("Artist Delete Request"))
                        .build())
        );
    }

    @Test
    void 아티스트를_id로_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        given(artistQueryService.getArtistById(anyLong()))
                .willReturn(ArtistFixture.create(1L));

        // when, then
        mockMvc.perform(get("/api/v1/artists/{artistId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(ArtistGetResponse.of(ArtistFixture.create(1L)))))
                .andDo(getArtistDocument());
    }

    private RestDocumentationResultHandler getArtistDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Artist API")
                        .summary("아티스트 단건 조회 API")
                        .requestHeaders(getAuthorizationHeader().optional())
                        .pathParameters(
                                parameterWithName("artistId").type(SimpleType.NUMBER)
                                        .description("아티스트 id")
                        )
                        .responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("아티스트 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("아티스트 이름"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                        .description("아티스트 이미지 url")
                        )
                        .responseSchema(Schema.schema("Artist Get Response"))
                        .build())
        );
    }

    @Test
    void 아티스트_리스트를_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        ArrayList<ArtistGetResponse> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            data.add(ArtistGetResponse.of(ArtistFixture.create()));
        }

        OffsetBasePaginatedResponse<ArtistGetResponse> response = new OffsetBasePaginatedResponse<>(
                0,
                true,
                data
        );

        given(artistQueryService.getArtistList(any()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber", "0")
                        .param("pageSize", "12"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getArtistListDocument());
    }

    private RestDocumentationResultHandler getArtistListDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Artist API")
                        .summary("아티스트 리스트 조회 API")
                        .requestHeaders(getAuthorizationHeader().optional())
                        .queryParameters(getOffsetBasePagingQueryParameters())
                        .responseFields(
                                fieldWithPath("pageNumber").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("현재 페이지 번호"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 데이터 존재 여부"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("아티스트 id"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING)
                                        .description("아티스트 이름"),
                                fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
                                        .description("아티스트 이미지 url")
                        )
                        .responseSchema(Schema.schema("Artist List Response"))
                        .build())
        );
    }

    @Test
    void 아티스트를_검색하면_데이터와_200응답을_해야_힌디() throws Exception {
        // given
        ArrayList<ArtistGetResponse> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            data.add(ArtistGetResponse.of(ArtistFixture.create()));
        }

        OffsetBasePaginatedResponse<ArtistGetResponse> response = new OffsetBasePaginatedResponse<>(
                0,
                true,
                data
        );

        given(artistQueryService.searchArtists(any(), any()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/artists/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("query", "artist")
                        .param("pageNumber", "0")
                        .param("pageSize", "12"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(print())
                .andDo(getArtistSearchDocument());
    }

    private RestDocumentationResultHandler getArtistSearchDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getOffsetBasePagingQueryParameters();

        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 1);
        queryParams[pagingQueryParameters.length] = parameterWithName("query")
                .type(SimpleType.STRING)
                .optional()
                .description("검색어 (null이거나 빈 값이면 빈 리스트 반환)");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Artist API")
                        .summary("아티스트 검색 API")
                        .requestHeaders(getAuthorizationHeader().optional())
                        .queryParameters(queryParams)
                        .responseFields(
                                fieldWithPath("pageNumber").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("현재 페이지 번호"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 데이터 존재 여부"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("아티스트 id"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("아티스트 이름"),
                                fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("아티스트 이미지 url")
                        )
                        .responseSchema(Schema.schema("Artist List Response"))
                        .build())
        );
    }
}
