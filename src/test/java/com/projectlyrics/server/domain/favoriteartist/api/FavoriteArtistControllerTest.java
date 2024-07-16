package com.projectlyrics.server.domain.favoriteartist.api;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.favoriteartist.dto.request.CreateFavoriteArtistListRequest;
import com.projectlyrics.server.support.RestDocsTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FavoriteArtistControllerTest extends RestDocsTest {

    @Test
    void 관심_아티스트들을_저장해야_한다() throws Exception {
        //given
        CreateFavoriteArtistListRequest request = new CreateFavoriteArtistListRequest(List.of(1L, 2L));

        //when then
        mockMvc.perform(post("/api/v1/favorite-artists/batch")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(getCreateFavoriteArtistListDocument());
    }

    private RestDocumentationResultHandler getCreateFavoriteArtistListDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Favorite Artist API")
                        .summary("관심 아티스트 리스트 추가 API")
                        .requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).type(SimpleType.STRING)
                                        .description("Bearer ${accessToken}")
                        )
                        .requestFields(
                                fieldWithPath("artistIds").type(JsonFieldType.ARRAY)
                                        .description("Artist Id 배열(number) ex) [1, 2, 3]")
                        )
                        .requestSchema(Schema.schema("Create Favorite Artist List Request"))
                        .build())
        );
    }
}
