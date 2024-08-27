package com.projectlyrics.server.domain.song.api;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.common.dto.util.OffsetBasePaginatedResponse;
import com.projectlyrics.server.domain.song.dto.response.SongSearchResponse;
import com.projectlyrics.server.support.RestDocsTest;
import com.projectlyrics.server.support.fixture.SongFixture;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.ArrayList;
import java.util.Arrays;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class
SongControllerTest extends RestDocsTest {

    @Test
    void 곡을_검색하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        ArrayList<SongSearchResponse> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            data.add(SongSearchResponse.from(SongFixture.create()));
        }

        OffsetBasePaginatedResponse<SongSearchResponse> response = new OffsetBasePaginatedResponse<>(
                0,
                true,
                data
        );

        given(songQueryService.searchSongs(any(), any(), anyInt(), anyInt()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/songs/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("query", "song")
                        .param("artistId", "1")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getSongSearchDocument());
    }

    private RestDocumentationResultHandler getSongSearchDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getOffsetBasePagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 2);

        queryParams[pagingQueryParameters.length] = parameterWithName("query")
                .type(SimpleType.STRING)
                .optional()
                .description("검색어가 없지만 아티스트 id는 있을 경우 아티스트 id 기준 노트 개수 역순으로 정렬된 곡 리스트를 반환합니다.");
        queryParams[pagingQueryParameters.length + 1] = parameterWithName("artistId")
                .type(SimpleType.NUMBER)
                .optional()
                .description("아티스트 id는 없지만 검색어만 있을 경우 검색어를 곡 제목에 포함하는 곡 리스트를 노트 개수 역순으로 정렬하여 반환합니다.");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Song API")
                        .summary("곡 검색 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(queryParams)
                        .responseFields(
                                fieldWithPath("pageNumber").type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("API 응답의 현재 페이지 번호"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 데이터 존재 여부"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("곡 id"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING)
                                        .description("곡 이름"),
                                fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
                                        .description("곡 앨범 이미지 url"),
                                fieldWithPath("data[].noteCount").type(JsonFieldType.NUMBER)
                                        .description("곡과 관련된 등록된 노트의 개수"),
                                fieldWithPath("data[].artist.id").type(JsonFieldType.NUMBER)
                                        .description("곡 아티스트의 id"),
                                fieldWithPath("data[].artist.name").type(JsonFieldType.STRING)
                                        .description("곡 아티스트의 이름"),
                                fieldWithPath("data[].artist.imageUrl").type(JsonFieldType.STRING)
                                        .description("곡 아티스트의 이미지 url")
                        )
                        .responseSchema(Schema.schema("Song Search Response"))
                        .build())
        );
    }
}