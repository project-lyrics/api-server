package com.projectlyrics.server.domain.auth.api;

import com.projectlyrics.server.common.ControllerTest;
import com.projectlyrics.server.domain.auth.dto.request.AuthUserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthLoginResponse;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends ControllerTest {

    @Test
    void 로그인할_때_소셜_로그인_인증된_유저는_인증_토큰과_200응답을_받아야_한다() throws Exception {
        //given
        AuthUserLoginRequest request = new AuthUserLoginRequest(AuthProvider.KAKAO);
        AuthLoginResponse response = new AuthLoginResponse("access token", "refresh token", false);
        String socialAccessToken = "socialAccessToken";
        given(authCommandService.signIn(any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + socialAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }
}
