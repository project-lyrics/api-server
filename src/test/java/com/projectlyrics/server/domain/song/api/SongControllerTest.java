package com.projectlyrics.server.domain.song.api;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.support.RestDocsTest;
import com.projectlyrics.server.support.fixture.SongFixture;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultHandler;

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

class SongControllerTest extends RestDocsTest {

    @Test
    void 곡을_검색하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        ArrayList<SongGetResponse> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            data.add(SongGetResponse.from(SongFixture.create()));
        }

        CursorBasePaginatedResponse<SongGetResponse> response = new CursorBasePaginatedResponse<>(
                data.get(data.size() - 1).id(),
                true,
                data
        );

        given(songQueryService.searchSongs(any(), any(), anyInt()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/songs/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("query", "song")
                        .param("cursor", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getSongSearchDocument());
    }

    private RestDocumentationResultHandler getSongSearchDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getPagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 1);

        queryParams[pagingQueryParameters.length] = parameterWithName("query")
                .type(SimpleType.STRING)
                .optional()
                .description("검색어 (null이거나 빈 값이면 빈 리스트 반환)");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Song API")
                        .summary("곡 검색 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(queryParams)
                        .responseFields(
                                fieldWithPath("nextCursor").type(JsonFieldType.NUMBER)
                                        .optional()
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
}