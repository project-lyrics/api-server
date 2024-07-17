package com.projectlyrics.server.domain.artist.api;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.support.ControllerTest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.support.RestDocsTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArtistControllerTest extends RestDocsTest {

    @Test
    void 아티스트_리스트를_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        //given
        ArrayList<ArtistGetResponse> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(ArtistGetResponse.of(ArtistFixture.createWithName("artist" + i)));
        }
        CursorBasePaginatedResponse<ArtistGetResponse> response = new CursorBasePaginatedResponse<>(
                data.get(data.size() - 1).id(),
                true,
                data
        );
        given(artistQueryService.getArtistList(any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(get("/api/v1/artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cursor", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(print())
                .andDo(getArtistListDocument());
    }

    private RestDocumentationResultHandler getArtistListDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Artist API")
                        .summary("아티스트 리스트 조회 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(getPagingQueryParameters())
                        .responseFields(
                                fieldWithPath("nextCursor").type(JsonFieldType.NUMBER)
                                        .description("다음 cursor에 쓰일 값"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 데이터 존재 여부"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("아티스트 id"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING)
                                        .description("아티스트 이름"),
                                fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
                                        .description("아티스트 이미지")
                        )
                        .responseSchema(Schema.schema("Artist List Response"))
                        .build())
        );
    }

    @Test
    void 아티스트를_추가해야_한다() throws Exception {
        //given
        ArtistAddRequest request = new ArtistAddRequest("라디오헤드", "https://lll.kk");

        //when then
        mockMvc.perform(post("/api/v1/artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void 아티스트를_수정해야_한다() throws Exception {
        //given
        ArtistUpdateRequest request = new ArtistUpdateRequest("라디오헤드", "https://kkk.ll");

        //when then
        mockMvc.perform(patch("/api/v1/artists/{artistId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void 아티스트를_검색하면_데이터와_200응답을_해야_힌디() throws Exception {
        //given
        ArrayList<ArtistGetResponse> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(ArtistGetResponse.of(ArtistFixture.createWithName("artist" + i)));
        }
        CursorBasePaginatedResponse<ArtistGetResponse> response = new CursorBasePaginatedResponse<>(
                data.get(data.size() - 1).id(),
                true,
                data
        );
        given(artistQueryService.searchArtists(any(), any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(get("/api/v1/artists/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("query", "artist")
                        .param("cursor", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(print())
                .andDo(getArtistSearchDocument());
    }

    @Test
    void 검색어가_null이거나_공백인_경우_아티스트를_검색하면_빈_데이터와_200응답을_해야_힌디() throws Exception {
        //given
        CursorBasePaginatedResponse<ArtistGetResponse> response = new CursorBasePaginatedResponse<>(
                null,
                false,
                new ArrayList<>()
        );

        //when then
        mockMvc.perform(get("/api/v1/artists/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("query", " ")
                        .param("cursor", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(print())
                .andDo(getArtistSearchDocument());
    }

    private RestDocumentationResultHandler getArtistSearchDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getPagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 1);
        queryParams[pagingQueryParameters.length] = parameterWithName("query")
                .type(SimpleType.STRING)
                .optional()
                .description("검색어 (null이거나 빈 값이면 빈 리스트 반환)");
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Artist API")
                        .summary("아티스트 검색 API")
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
                                        .optional()
                                        .description("아티스트 id"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("아티스트 이름"),
                                fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("아티스트 이미지")
                        )
                        .responseSchema(Schema.schema("Artist List Response"))
                        .build())
        );
    }
}
