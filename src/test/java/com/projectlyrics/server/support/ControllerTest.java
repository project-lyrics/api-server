package com.projectlyrics.server.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectlyrics.server.domain.artist.service.ArtistCommandService;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.TokenExtractor;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtExtractor;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtProvider;
import com.projectlyrics.server.domain.auth.authentication.jwt.AuthToken;
import com.projectlyrics.server.domain.auth.service.AuthCommandService;
import com.projectlyrics.server.domain.auth.service.AuthQueryService;
import com.projectlyrics.server.domain.bookmark.service.BookmarkCommandService;
import com.projectlyrics.server.domain.comment.service.CommentCommandService;
import com.projectlyrics.server.domain.favoriteartist.service.FavoriteArtistCommandService;
import com.projectlyrics.server.domain.favoriteartist.service.FavoriteArtistQueryService;
import com.projectlyrics.server.domain.like.service.LikeCommandService;
import com.projectlyrics.server.domain.like.service.LikeQueryService;
import com.projectlyrics.server.domain.note.service.NoteCommandService;
import com.projectlyrics.server.domain.note.service.NoteQueryService;
import com.projectlyrics.server.domain.user.service.UserCommandService;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import com.projectlyrics.server.global.configuration.ClockConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import({
        ClockConfig.class,
        AuthContext.class,
        JwtExtractor.class,
        JwtProvider.class,
        TokenExtractor.class,
})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    protected JwtProvider jwtProvider;

    @MockBean
    protected ArtistCommandService artistCommandService;

    @MockBean
    protected ArtistQueryService artistQueryService;

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

    @MockBean
    protected FavoriteArtistQueryService favoriteArtistQueryService;

    @MockBean
    protected NoteCommandService noteCommandService;

    @MockBean
    protected NoteQueryService noteQueryService;

    @MockBean
    protected CommentCommandService commentCommandService;

    @MockBean
    protected LikeCommandService likeCommandService;

    @MockBean
    protected LikeQueryService likeQueryService;

    @MockBean
    protected BookmarkCommandService bookmarkCommandService;

    public String accessToken;
    public String refreshToken;

    @BeforeEach
    public void setUp() {
        AuthToken authToken = jwtProvider.issueTokens(1L, "nickname");
        accessToken = authToken.accessToken();
        refreshToken = authToken.refreshToken();
    }
}
