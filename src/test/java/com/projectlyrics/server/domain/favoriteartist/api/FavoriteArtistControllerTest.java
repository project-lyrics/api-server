package com.projectlyrics.server.domain.favoriteartist.api;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.favoriteartist.dto.response.FavoriteArtistCreateResponse;
import com.projectlyrics.server.domain.favoriteartist.dto.response.FavoriteArtistResponse;
import com.projectlyrics.server.domain.favoriteartist.dto.request.CreateFavoriteArtistListRequest;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.support.RestDocsTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.FavoriteArtistFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultHandler;

import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FavoriteArtistControllerTest extends RestDocsTest {

    @Test
    void 관심_아티스트를_저장하면_데이터와_200응답을_해야_한다() throws Exception {
        // when, then
        mockMvc.perform(post("/api/v1/favorite-artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .queryParam("artistId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new FavoriteArtistCreateResponse(true))))
                .andDo(getCreateFavoriteArtistDocument());
    }

    private RestDocumentationResultHandler getCreateFavoriteArtistDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Favorite Artist API")
                        .summary("관심 아티스트 추가 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(
                                parameterWithName("artistId").type(SimpleType.NUMBER)
                                        .description("아티스트 id")
                        )
                        .requestSchema(Schema.schema("Create Favorite Artist Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Create Favorite Artist Response"))
                        .build())

        );
    }

    @Test
    void 관심_아티스트들을_저장하면_200응답을_해야_한다() throws Exception {
        //given
        CreateFavoriteArtistListRequest request = new CreateFavoriteArtistListRequest(List.of(1L, 2L));

        //when then
        mockMvc.perform(post("/api/v1/favorite-artists/batch")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new FavoriteArtistCreateResponse(true))))
                .andDo(getCreateFavoriteArtistListDocument());
    }

    private RestDocumentationResultHandler getCreateFavoriteArtistListDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Favorite Artist API")
                        .summary("관심 아티스트 리스트 추가 API")
                        .requestHeaders(getAuthorizationHeader())
                        .requestFields(
                                fieldWithPath("artistIds").type(JsonFieldType.ARRAY)
                                        .description("Artist Id 배열(number) ex) [1, 2, 3]")
                        )
                        .requestSchema(Schema.schema("Create Favorite Artist List Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Create Favorite Artist List Response"))
                        .build())
        );
    }

    @Test
    void 관심_아티스트를_삭제하면_200응답을_해야_한다() throws Exception {
        // when, then
        mockMvc.perform(delete("/api/v1/favorite-artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .queryParam("artistId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getDeleteFavoriteArtistDocument());
    }

    private RestDocumentationResultHandler getDeleteFavoriteArtistDocument() {
        ParameterDescriptorWithType[] queryParam = {
                parameterWithName("artistId").type(SimpleType.NUMBER)
                        .description("아티스트 id")
        };

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Favorite Artist API")
                        .summary("관심 아티스트 삭제 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(queryParam)
                        .requestSchema(Schema.schema("Delete Favorite Artist Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Delete Favorite Artist Response"))
                        .build()
                )
        );
    }

    @Test
    void 관심_아티스트를_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        //given
        ArrayList<FavoriteArtistResponse> data = new ArrayList<>();
        User user = UserFixture.create();
        for (int i = 0; i < 10; i++) {
            Artist artist = ArtistFixture.create();
            data.add(FavoriteArtistResponse.of(FavoriteArtistFixture.create(user, artist)));
        }
        CursorBasePaginatedResponse<FavoriteArtistResponse> response = new CursorBasePaginatedResponse<>(
                data.get(data.size() - 1).id(),
                true,
                data
        );
        given(favoriteArtistQueryService.findFavoriteArtists(any(), any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(get("/api/v1/favorite-artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("cursor", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getFavoriteArtistListDocument());
    }

    private RestDocumentationResultHandler getFavoriteArtistListDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Favorite Artist API")
                        .summary("관심 아티스트 리스트 조회 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(getCursorBasePagingQueryParameters())
                        .responseFields(
                                fieldWithPath("nextCursor").type(JsonFieldType.NUMBER)
                                        .description("다음 cursor에 쓰일 값"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 데이터 존재 여부"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("관심 아티스트 id"),
                                fieldWithPath("data[].artist.id").type(JsonFieldType.NUMBER)
                                        .description("아티스트 id"),
                                fieldWithPath("data[].artist.name").type(JsonFieldType.STRING)
                                        .description("아티스트 이름"),
                                fieldWithPath("data[].artist.imageUrl").type(JsonFieldType.STRING)
                                        .description("아티스트 이미지")
                        )
                        .responseSchema(Schema.schema("Favorite Artist List Response"))
                        .build())
        );
    }

    @Test
    void 노트를_작성한_관심_아티스트를_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        List<FavoriteArtistResponse> response = new ArrayList<>();
        User user = UserFixture.create();
        for (int i = 0; i < 10; i++) {
            Artist artist = ArtistFixture.create();
            response.add(FavoriteArtistResponse.of(FavoriteArtistFixture.create(user, artist)));
        }
        given(favoriteArtistQueryService.findAllHavingNotesOfUser(any()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/favorite-artists/having-notes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getFavoriteArtistHavingNotesOfUserDocument());
    }

    private RestDocumentationResultHandler getFavoriteArtistHavingNotesOfUserDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Favorite Artist API")
                        .summary("사용자가 노트를 작성한 관심 아티스트 리스트 조회 API")
                        .requestHeaders(getAuthorizationHeader())
                        .responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER)
                                        .description("관심 아티스트 id"),
                                fieldWithPath("[].artist.id").type(JsonFieldType.NUMBER)
                                        .description("아티스트 id"),
                                fieldWithPath("[].artist.name").type(JsonFieldType.STRING)
                                        .description("아티스트 이름"),
                                fieldWithPath("[].artist.imageUrl").type(JsonFieldType.STRING)
                                        .description("아티스트 이미지")
                        )
                        .responseSchema(Schema.schema("Favorite Artist Having Notes Of User Response"))
                        .build())
        );
    }

    @Test
    void 북마크한_노트와_연관된_관심_아티스트를_조회하면_데이터와_200응답을_해야_한다() throws Exception {

        List<FavoriteArtistResponse> response = new ArrayList<>();
        User user = UserFixture.create();
        for (int i = 0; i < 10; i++) {
            Artist artist = ArtistFixture.create();
            response.add(FavoriteArtistResponse.of(FavoriteArtistFixture.create(user, artist)));
        }
        given(favoriteArtistQueryService.findAllBookmarked(any()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/favorite-artists/bookmarked")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andDo(getFavoriteArtistBookmarkedDocument());
    }

    private RestDocumentationResultHandler getFavoriteArtistBookmarkedDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Favorite Artist API")
                        .summary("사용자가 북마크한 노트와 연관된 관심 아티스트 리스트 조회 API")
                        .requestHeaders(getAuthorizationHeader())
                        .responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER)
                                        .description("관심 아티스트 id"),
                                fieldWithPath("[].artist.id").type(JsonFieldType.NUMBER)
                                        .description("아티스트 id"),
                                fieldWithPath("[].artist.name").type(JsonFieldType.STRING)
                                        .description("아티스트 이름"),
                                fieldWithPath("[].artist.imageUrl").type(JsonFieldType.STRING)
                                        .description("아티스트 이미지")
                        )
                        .responseSchema(Schema.schema("Favorite Artist Bookmarked Response"))
                        .build())
        );
    }

    @Test
    void 관심_아티스트_여부를_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        // when
        mockMvc.perform(get("/api/v1/favorite-artists/exists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("artistId", "1"))
                .andExpect(status().isOk())
                .andDo(getFavoriteArtistExistsDocument());
    }

    private RestDocumentationResultHandler getFavoriteArtistExistsDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Favorite Artist API")
                        .summary("아티스트의 좋아하는 아티스트 등록 여부 조회 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(parameterWithName("artistId").type(SimpleType.NUMBER)
                                .description("아티스트 id"))
                        .responseFields(
                                fieldWithPath("exists").type(JsonFieldType.BOOLEAN)
                                        .description("관심 아티스트 존재 여부")
                        )
                        .responseSchema(Schema.schema("Favorite Artist Exists Response"))
                        .build())
        );
    }
}
