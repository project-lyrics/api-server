package com.projectlyrics.server.domain.favoriteartist.api;

import com.projectlyrics.server.support.ControllerTest;
import com.projectlyrics.server.domain.favoriteartist.dto.request.CreateFavoriteArtistListRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FavoriteArtistControllerTest extends ControllerTest {

    @Test
    void 관심_아티스트들을_저장해야_한다() throws Exception {
        //given
        CreateFavoriteArtistListRequest request = new CreateFavoriteArtistListRequest(List.of(1L, 2L));

        //when then
        mockMvc.perform(post("/api/v1/favorite-artists/batch")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
