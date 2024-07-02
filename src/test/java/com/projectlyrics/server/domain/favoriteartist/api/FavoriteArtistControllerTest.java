package com.projectlyrics.server.domain.favoriteartist.api;

import com.projectlyrics.server.common.ControllerTest;
import com.projectlyrics.server.domain.favoriteartist.dto.request.CreateFavoriteArtistListRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FavoriteArtistControllerTest extends ControllerTest {

    @Test
    @WithMockUser
    void 관심_아티스트들을_저장해야_한다() throws Exception {
        //given
        CreateFavoriteArtistListRequest request = new CreateFavoriteArtistListRequest(List.of(1L, 2L));

        //when then
        mockMvc.perform(post("/api/v1/favorite-artists/batch")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
