package com.projectlyrics.server.domain.song.api;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.common.dto.util.OffsetBasePaginatedResponse;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.song.dto.response.SongSearchResponse;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.support.RestDocsTest;
import com.projectlyrics.server.support.fixture.SongFixture;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

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

        given(songQueryService.searchSongs(any(), anyInt(), anyInt()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/songs/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("query", "song")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getSongSearchDocument());
    }

    private RestDocumentationResultHandler getSongSearchDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getOffsetBasePagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 1);

        queryParams[pagingQueryParameters.length] = parameterWithName("query")
                .type(SimpleType.STRING)
                .optional()
                .description("검색어가 없지만 아티스트 id는 있을 경우 아티스트 id 기준 노트 개수 역순으로 정렬된 곡 리스트를 반환합니다.");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Song API")
                        .summary("노트 개수 역순 정렬 곡 검색 API")
                        .requestHeaders(getAuthorizationHeader().optional())
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

    @Test
    void 곡을_아티스트별로_검색하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        ArrayList<SongGetResponse> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            data.add(SongGetResponse.from(SongFixture.create()));
        }

        CursorBasePaginatedResponse<SongGetResponse> response = new CursorBasePaginatedResponse<>(
                11L,
                true,
                data
        );

        given(songQueryService.searchSongsByArtist(any(), any(), any(), anyInt()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/songs/search/artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("artistId", "1")
                        .param("query", "song")
                        .param("cursor", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getSongSearchByArtistDocument());
    }

    private RestDocumentationResultHandler getSongSearchByArtistDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getCursorBasePagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 2);

        queryParams[pagingQueryParameters.length] = parameterWithName("query")
                .type(SimpleType.STRING)
                .optional()
                .description("검색어가 없지만 아티스트 id는 있을 경우 아티스트 id 기준 노트 개수 역순으로 정렬된 곡 리스트를 반환합니다.");
        queryParams[pagingQueryParameters.length + 1] = parameterWithName("artistId")
                .type(SimpleType.NUMBER)
                .description("아티스트 id");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Song API")
                        .summary("아티스트 id 기반 최신순 정렬 곡 검색 API")
                        .requestHeaders(getAuthorizationHeader().optional())
                        .queryParameters(queryParams)
                        .responseFields(
                                fieldWithPath("nextCursor").type(JsonFieldType.NUMBER)
                                        .description("다음 cursor에 쓰일 값"),
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

    @Test
    void 곡_id로_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        Song song = SongFixture.create();

        given(songQueryService.getById(any()))
                .willReturn(SongSearchResponse.from(song));

        // when, then
        mockMvc.perform(get("/api/v1/songs/{songId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getSongDocument());
    }

    private RestDocumentationResultHandler getSongDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Song API")
                        .summary("곡 id로 곡 정보 검색 API")
                        .requestHeaders(getAuthorizationHeader().optional())
                        .pathParameters(
                                parameterWithName("songId").type(SimpleType.NUMBER)
                                        .description("곡 ID")
                        )
                        .responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("곡 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("곡 이름"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING)
                                        .description("곡 앨범 이미지 url"),
                                fieldWithPath("noteCount").type(JsonFieldType.NUMBER)
                                        .description("곡과 관련된 등록된 노트의 개수"),
                                fieldWithPath("artist.id").type(JsonFieldType.NUMBER)
                                        .description("곡 아티스트의 id"),
                                fieldWithPath("artist.name").type(JsonFieldType.STRING)
                                        .description("곡 아티스트의 이름"),
                                fieldWithPath("artist.imageUrl").type(JsonFieldType.STRING)
                                        .description("곡 아티스트의 이미지 url")
                        )
                        .responseSchema(Schema.schema("Song Get Response"))
                        .build())
        );
    }
}