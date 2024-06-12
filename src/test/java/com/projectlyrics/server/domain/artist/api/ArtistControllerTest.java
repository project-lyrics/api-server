package com.projectlyrics.server.domain.artist.api;

import com.projectlyrics.server.common.ControllerTest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.dto.response.ArtistAddResponse;
import com.projectlyrics.server.domain.common.dto.SuccessResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArtistControllerTest extends ControllerTest {

  @Test
  void 아티스트를_추가해야_한다() throws Exception {
    //given
    ArtistAddRequest request = new ArtistAddRequest("라디오헤드", "radiohead", "http://~");

    //when then
    mockMvc.perform(post("/api/v1/artists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void 아티스트를_수정해야_한다() throws Exception {
    //given
    ArtistUpdateRequest request = new ArtistUpdateRequest("라디오헤드", "radiohead", "http://~");

    //when then
    mockMvc.perform(patch("/api/v1/artists/{artistId}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }
}
