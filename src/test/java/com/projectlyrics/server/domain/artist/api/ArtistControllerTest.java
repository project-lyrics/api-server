package com.projectlyrics.server.domain.artist.api;

import com.projectlyrics.server.common.ControllerTest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArtistControllerTest extends ControllerTest {

    @Test
    @WithMockUser
    void 아티스트를_추가해야_한다() throws Exception {
        //given
        ArtistAddRequest request = new ArtistAddRequest("라디오헤드", "radiohead", "https://lll.kk");

        //when then
        mockMvc.perform(post("/api/v1/artists")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void 아티스트를_수정해야_한다() throws Exception {
        //given
        ArtistUpdateRequest request = new ArtistUpdateRequest("라디오헤드", "radiohead", "https://kkk.ll");

        //when then
        mockMvc.perform(patch("/api/v1/artists/{artistId}", 1)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
