package com.projectlyrics.server.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectlyrics.server.domain.artist.service.ArtistCommandService;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.domain.auth.authentication.JwtAuthenticationEntryPoint;
import com.projectlyrics.server.domain.auth.authentication.JwtAuthenticationFilter;
import com.projectlyrics.server.domain.auth.authentication.UndefinedAccessHandler;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;
import com.projectlyrics.server.domain.auth.service.AuthCommandService;
import com.projectlyrics.server.domain.auth.service.AuthQueryService;
import com.projectlyrics.server.domain.favoriteartist.service.FavoriteArtistCommandService;
import com.projectlyrics.server.domain.user.service.UserCommandService;
import com.projectlyrics.server.domain.record.service.RecordCommandService;
import com.projectlyrics.server.domain.record.service.RecordQueryService;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import com.projectlyrics.server.global.configuration.SecurityConfig;
import com.projectlyrics.server.global.handler.FilterExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import({
        JwtTokenProvider.class,
        FilterExceptionHandler.class,
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        UndefinedAccessHandler.class
})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    protected ArtistCommandService artistCommandService;

    @MockBean
    protected ArtistQueryService artistQueryService;

    @MockBean
    protected RecordCommandService recordCommandService;

    @MockBean
    protected RecordQueryService recordQueryService;

    @MockBean
    protected UserQueryService userQueryService;

    @MockBean
    protected UserCommandService userCommandService;

    @MockBean
    protected AuthCommandService authCommandService;

    @MockBean
    protected AuthQueryService authQueryService;

    @MockBean
    protected FavoriteArtistCommandService favoriteArtistCommandService;

    protected String accessToken;

    @BeforeEach
    public void setUp() {
        AuthToken authToken = jwtTokenProvider.issueTokens(1L);
        accessToken = authToken.accessToken();
    }
}
