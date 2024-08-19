package com.projectlyrics.server.domain.note.api;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.dto.request.NoteUpdateRequest;
import com.projectlyrics.server.domain.note.dto.response.NoteDetailResponse;
import com.projectlyrics.server.domain.note.dto.response.NoteGetResponse;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.support.RestDocsTest;
import com.projectlyrics.server.support.fixture.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NoteControllerTest extends RestDocsTest {

    @Test
    void 노트를_저장하면_200응답을_해야_한다() throws Exception {
        // given
        NoteCreateRequest request = new NoteCreateRequest(
                "초불소! 초불소! 초!불!소",
                "나의 꽃이 피는 날이 올 수 있을까",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                1L
        );

        // when, then
        mockMvc.perform(post("/api/v1/notes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(getNoteCreateDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getNoteCreateDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Note API")
                        .summary("노트 등록 API")
                        .requestHeaders(getAuthorizationHeader())
                        .requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("노트 내용"),
                                fieldWithPath("lyrics").type(JsonFieldType.STRING)
                                        .description("가사 내용")
                                        .optional(),
                                fieldWithPath("background").type(JsonFieldType.STRING)
                                        .description("가사 배경색" + getEnumValuesAsString(NoteBackground.class))
                                        .optional(),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("노트 등록 상태" + getEnumValuesAsString(NoteStatus.class)),
                                fieldWithPath("songId").type(JsonFieldType.NUMBER)
                                        .description("곡 ID")
                        )
                        .requestSchema(Schema.schema("Create Note Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Create Note Response"))
                        .build())
        );
    }

    @Test
    void 노트를_수정하면_200응답을_해야_한다() throws Exception {
        // given
        NoteUpdateRequest noteUpdateRequest = new NoteUpdateRequest(
                "검치는 언제쯤 질릴까",
                "밖으로 보이는 조그만 점이, 먼지만큼 작아지도록",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED
        );

        // when, then
        mockMvc.perform(patch("/api/v1/notes/{noteId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(noteUpdateRequest)))
                .andDo(getNoteUpdateDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getNoteUpdateDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Note API")
                        .summary("노트 수정 API")
                        .requestHeaders(getAuthorizationHeader())
                        .pathParameters(
                                parameterWithName("noteId").type(SimpleType.NUMBER)
                                        .description("노트 ID")
                        )
                        .requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("노트 내용"),
                                fieldWithPath("lyrics").type(JsonFieldType.STRING)
                                        .description("가사 내용")
                                        .optional(),
                                fieldWithPath("background").type(JsonFieldType.STRING)
                                        .description("가사 배경색")
                                        .optional(),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("노트 등록 상태" + getEnumValuesAsString(NoteStatus.class))
                        )
                        .requestSchema(Schema.schema("Update Note Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Update Note Response"))
                        .build())
        );
    }

    @Test
    void 노트를_삭제하면_200응답을_해야_한다() throws Exception {
        // when, then
        mockMvc.perform(delete("/api/v1/notes/{noteId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(getNoteDeleteDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getNoteDeleteDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Note API")
                        .summary("노트 삭제 API")
                        .requestHeaders(getAuthorizationHeader())
                        .pathParameters(
                                parameterWithName("noteId").type(SimpleType.NUMBER)
                                        .description("노트 ID")
                        )
                        .requestSchema(Schema.schema("Delete Note Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Delete Note Response"))
                        .build())
        );
    }

    @Test
    void 노트를_단건으로_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        User user = UserFixture.create();
        Note note = NoteFixture.create(user, SongFixture.create(ArtistFixture.create()));

        given(noteQueryService.getNoteById(any(), any()))
                .willReturn(NoteDetailResponse.of(note, List.of(CommentFixture.create(note, user)), user.getId(), LocalDateTime.now()));

        // when, then
        mockMvc.perform(get("/api/v1/notes/{noteId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getNoteDetailDocument());
    }

    private RestDocumentationResultHandler getNoteDetailDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Note API")
                        .summary("노트 단건 조회 API")
                        .requestHeaders(getAuthorizationHeader())
                        .pathParameters(
                                parameterWithName("noteId").type(SimpleType.NUMBER)
                                        .description("노트 ID")
                        )
                        .responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("노트 Id"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("노트 내용"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("노트 등록 상태" + getEnumValuesAsString(NoteStatus.class)),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                        .description("노트 생성 시간"),
                                fieldWithPath("lyrics.lyrics").type(JsonFieldType.STRING)
                                        .description("가사 내용")
                                        .optional(),
                                fieldWithPath("lyrics.background").type(JsonFieldType.STRING)
                                        .description("가사 배경색" + getEnumValuesAsString(NoteBackground.class))
                                        .optional(),
                                fieldWithPath("publisher.id").type(JsonFieldType.NUMBER)
                                        .description("게시자 Id"),
                                fieldWithPath("publisher.nickname").type(JsonFieldType.STRING)
                                        .description("게시자 닉네임"),
                                fieldWithPath("publisher.profileCharacterType").type(JsonFieldType.STRING)
                                        .description("게시자 프로필 이미지 타입" + getEnumValuesAsString(ProfileCharacter.class)),
                                fieldWithPath("song.id").type(JsonFieldType.NUMBER)
                                        .description("곡 Id"),
                                fieldWithPath("song.name").type(JsonFieldType.STRING)
                                        .description("곡 제목"),
                                fieldWithPath("song.imageUrl").type(JsonFieldType.STRING)
                                        .description("곡 이미지 url"),
                                fieldWithPath("song.artist.id").type(JsonFieldType.NUMBER)
                                        .description("곡 아티스트의 id"),
                                fieldWithPath("song.artist.name").type(JsonFieldType.STRING)
                                        .description("곡 아티스트의 이름"),
                                fieldWithPath("song.artist.imageUrl").type(JsonFieldType.STRING)
                                        .description("곡 아티스트의 이미지 url"),
                                fieldWithPath("commentsCount").type(JsonFieldType.NUMBER)
                                        .description("댓글 개수"),
                                fieldWithPath("comments").type(JsonFieldType.ARRAY)
                                        .description("댓글 리스트"),
                                fieldWithPath("comments[].id").type(JsonFieldType.NUMBER)
                                        .description("댓글 Id"),
                                fieldWithPath("comments[].content").type(JsonFieldType.STRING)
                                        .description("댓글 내용"),
                                fieldWithPath("comments[].createdAt").type(JsonFieldType.STRING)
                                        .description("댓글 생성 시간"),
                                fieldWithPath("comments[].writer.id").type(JsonFieldType.NUMBER)
                                        .description("댓글 게시자 Id"),
                                fieldWithPath("comments[].writer.nickname").type(JsonFieldType.STRING)
                                        .description("댓글 게시자 닉네임"),
                                fieldWithPath("comments[].writer.profileCharacterType").type(JsonFieldType.STRING)
                                        .description("댓글 게시자 프로필 이미지 타입" + getEnumValuesAsString(ProfileCharacter.class)),
                                fieldWithPath("likesCount").type(JsonFieldType.NUMBER)
                                        .description("좋아요 개수"),
                                fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN)
                                        .description("사용자의 해당 게시물 좋아요 여부"),
                                fieldWithPath("isBookmarked").type(JsonFieldType.BOOLEAN)
                                        .description("사용자의 해당 게시물 북마크 여부")
                        )
                        .requestSchema(Schema.schema("Note Detail Response"))
                        .build())
        );
    }

    @Test
    void 사용자의_노트_리스트를_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        User user = UserFixture.create();

        List<NoteGetResponse> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Note note = NoteFixture.create(user, SongFixture.create(ArtistFixture.create()));
            data.add(NoteGetResponse.of(note, user.getId(), LocalDateTime.now()));
        }

        CursorBasePaginatedResponse<NoteGetResponse> response = new CursorBasePaginatedResponse<>(
                data.get(data.size() - 1).getId(),
                true,
                data
        );

        given(noteQueryService.getNotesByUserId(any(), any(), anyInt()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/notes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("cursor", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getNoteListDocument());
    }

    @Test
    void 사용자가_좋아하는_아티스트들과_관련된_노트_리스트를_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        User user = UserFixture.create();

        List<NoteGetResponse> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Note note = NoteFixture.create(user, SongFixture.create(ArtistFixture.create()));
            data.add(NoteGetResponse.of(note, user.getId(), LocalDateTime.now()));
        }

        CursorBasePaginatedResponse<NoteGetResponse> response = new CursorBasePaginatedResponse<>(
                data.get(data.size() - 1).getId(),
                true,
                data
        );

        given(noteQueryService.getNotesOfFavoriteArtists(any(), any(), anyInt()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/notes/favorite-artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("cursor", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getNoteListDocument());
    }

    private RestDocumentationResultHandler getNoteListDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Note API")
                        .summary("노트 리스트 조회 API")
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
                                        .description("노트 Id"),
                                fieldWithPath("data[].content").type(JsonFieldType.STRING)
                                        .description("노트 내용"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING)
                                        .description("노트 등록 상태" + getEnumValuesAsString(NoteStatus.class)),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
                                        .description("노트 생성 시간"),
                                fieldWithPath("data[].lyrics.lyrics").type(JsonFieldType.STRING)
                                        .description("가사 내용")
                                        .optional(),
                                fieldWithPath("data[].lyrics.background").type(JsonFieldType.STRING)
                                        .description("가사 배경색" + getEnumValuesAsString(NoteBackground.class))
                                        .optional(),
                                fieldWithPath("data[].publisher.id").type(JsonFieldType.NUMBER)
                                        .description("게시자 Id"),
                                fieldWithPath("data[].publisher.nickname").type(JsonFieldType.STRING)
                                        .description("게시자 닉네임"),
                                fieldWithPath("data[].publisher.profileCharacterType").type(JsonFieldType.STRING)
                                        .description("게시자 프로필 이미지 타입" + getEnumValuesAsString(ProfileCharacter.class)),
                                fieldWithPath("data[].song.id").type(JsonFieldType.NUMBER)
                                        .description("곡 Id"),
                                fieldWithPath("data[].song.name").type(JsonFieldType.STRING)
                                        .description("곡 제목"),
                                fieldWithPath("data[].song.imageUrl").type(JsonFieldType.STRING)
                                        .description("곡 이미지 url"),
                                fieldWithPath("data[].song.artist.id").type(JsonFieldType.NUMBER)
                                        .description("곡 아티스트의 id"),
                                fieldWithPath("data[].song.artist.name").type(JsonFieldType.STRING)
                                        .description("곡 아티스트의 이름"),
                                fieldWithPath("data[].song.artist.imageUrl").type(JsonFieldType.STRING)
                                        .description("곡 아티스트의 이미지 url"),
                                fieldWithPath("data[].commentsCount").type(JsonFieldType.NUMBER)
                                        .description("댓글 개수"),
                                fieldWithPath("data[].likesCount").type(JsonFieldType.NUMBER)
                                        .description("좋아요 개수"),
                                fieldWithPath("data[].isLiked").type(JsonFieldType.BOOLEAN)
                                        .description("사용자의 해당 게시물 좋아요 여부"),
                                fieldWithPath("data[].isBookmarked").type(JsonFieldType.BOOLEAN)
                                        .description("사용자의 해당 게시물 북마크 여부")
                        )
                        .responseSchema(Schema.schema("Note List Response"))
                        .build()
                )
        );
    }

    @Test
    void 아티스트와_관련된_노트_리스트를_조회하면_데이터와_200응답을_해야_한다() throws Exception {
        // given
        User user = UserFixture.create();

        List<NoteGetResponse> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Note note = NoteFixture.create(user, SongFixture.create(ArtistFixture.create()));
            data.add(NoteGetResponse.of(note, user.getId(), LocalDateTime.now()));
        }

        CursorBasePaginatedResponse<NoteGetResponse> response = new CursorBasePaginatedResponse<>(
                data.get(data.size() - 1).getId(),
                true,
                data
        );

        given(noteQueryService.getNotesByArtistId(any(), any(), anyBoolean(), any(), anyInt()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/notes/artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("artistId", "1")
                        .param("hasLyrics", "false")
                        .param("cursor", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getArtistNoteListDocument());
    }

    private RestDocumentationResultHandler getArtistNoteListDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getPagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 2);
        queryParams[pagingQueryParameters.length] = parameterWithName("artistId")
                .type(SimpleType.NUMBER)
                .description("아티스트 Id");
        queryParams[pagingQueryParameters.length + 1] = parameterWithName("hasLyrics")
                .type(SimpleType.BOOLEAN)
                .optional()
                .description("가사가 있는 노트만 조회");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Note API")
                        .summary("특정 아티스트와 관련된 노트 리스트 조회 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(queryParams)
                        .responseFields(
                                fieldWithPath("nextCursor").type(JsonFieldType.NUMBER)
                                        .description("다음 cursor에 쓰일 값"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 데이터 존재 여부"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("노트 Id"),
                                fieldWithPath("data[].content").type(JsonFieldType.STRING)
                                        .description("노트 내용"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING)
                                        .description("노트 등록 상태" + getEnumValuesAsString(NoteStatus.class)),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
                                        .description("노트 생성 시간"),
                                fieldWithPath("data[].lyrics.lyrics").type(JsonFieldType.STRING)
                                        .description("가사 내용")
                                        .optional(),
                                fieldWithPath("data[].lyrics.background").type(JsonFieldType.STRING)
                                        .description("가사 배경색" + getEnumValuesAsString(NoteBackground.class))
                                        .optional(),
                                fieldWithPath("data[].publisher.id").type(JsonFieldType.NUMBER)
                                        .description("게시자 Id"),
                                fieldWithPath("data[].publisher.nickname").type(JsonFieldType.STRING)
                                        .description("게시자 닉네임"),
                                fieldWithPath("data[].publisher.profileCharacterType").type(JsonFieldType.STRING)
                                        .description("게시자 프로필 이미지 타입" + getEnumValuesAsString(ProfileCharacter.class)),
                                fieldWithPath("data[].song.id").type(JsonFieldType.NUMBER)
                                        .description("곡 Id"),
                                fieldWithPath("data[].song.name").type(JsonFieldType.STRING)
                                        .description("곡 제목"),
                                fieldWithPath("data[].song.imageUrl").type(JsonFieldType.STRING)
                                        .description("곡 이미지 url"),
                                fieldWithPath("data[].song.artist.id").type(JsonFieldType.NUMBER)
                                        .description("곡 아티스트의 id"),
                                fieldWithPath("data[].song.artist.name").type(JsonFieldType.STRING)
                                        .description("곡 아티스트의 이름"),
                                fieldWithPath("data[].song.artist.imageUrl").type(JsonFieldType.STRING)
                                        .description("곡 아티스트의 이미지 url"),
                                fieldWithPath("data[].commentsCount").type(JsonFieldType.NUMBER)
                                        .description("댓글 개수"),
                                fieldWithPath("data[].likesCount").type(JsonFieldType.NUMBER)
                                        .description("좋아요 개수"),
                                fieldWithPath("data[].isLiked").type(JsonFieldType.BOOLEAN)
                                        .description("사용자의 해당 게시물 좋아요 여부"),
                                fieldWithPath("data[].isBookmarked").type(JsonFieldType.BOOLEAN)
                                        .description("사용자의 해당 게시물 북마크 여부")
                        )
                        .responseSchema(Schema.schema("Artist's Note List Response"))
                        .build()
                )
        );
    }

    @Test
    void 사용자가_북마크했던_노트를_조회하면_200응답과_데이터를_반환해야_한다() throws Exception {
        // given
        User user = UserFixture.create();

        List<NoteGetResponse> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Note note = NoteFixture.create(user, SongFixture.create(ArtistFixture.create()));
            data.add(NoteGetResponse.of(note, user.getId(), LocalDateTime.now()));
        }

        CursorBasePaginatedResponse<NoteGetResponse> response = new CursorBasePaginatedResponse<>(
                data.get(data.size() - 1).getId(),
                true,
                data
        );

        given(noteQueryService.getBookmarkedNotes(any(), any(), any(), anyInt()))
                .willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/notes/bookmarked")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("artistId", "1")
                        .param("cursor", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(getBookmarkedNoteListDocument());
    }

    private RestDocumentationResultHandler getBookmarkedNoteListDocument() {
        ParameterDescriptorWithType[] pagingQueryParameters = getPagingQueryParameters();
        ParameterDescriptorWithType[] queryParams = Arrays.copyOf(pagingQueryParameters, pagingQueryParameters.length + 1);
        queryParams[pagingQueryParameters.length] = parameterWithName("artistId")
                .type(SimpleType.NUMBER)
                .description("아티스트 Id");

        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Note API")
                        .summary("북마크된 노트 리스트 조회 API")
                        .requestHeaders(getAuthorizationHeader())
                        .queryParameters(queryParams)
                        .responseFields(
                                fieldWithPath("nextCursor").type(JsonFieldType.NUMBER)
                                        .description("다음 cursor에 쓰일 값"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 데이터 존재 여부"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY)
                                        .description("데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                                        .description("노트 Id"),
                                fieldWithPath("data[].content").type(JsonFieldType.STRING)
                                        .description("노트 내용"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING)
                                        .description("노트 등록 상태" + getEnumValuesAsString(NoteStatus.class)),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
                                        .description("노트 생성 시간"),
                                fieldWithPath("data[].lyrics.lyrics").type(JsonFieldType.STRING)
                                        .description("가사 내용")
                                        .optional(),
                                fieldWithPath("data[].lyrics.background").type(JsonFieldType.STRING)
                                        .description("가사 배경색" + getEnumValuesAsString(NoteBackground.class))
                                        .optional(),
                                fieldWithPath("data[].publisher.id").type(JsonFieldType.NUMBER)
                                        .description("게시자 Id"),
                                fieldWithPath("data[].publisher.nickname").type(JsonFieldType.STRING)
                                        .description("게시자 닉네임"),
                                fieldWithPath("data[].publisher.profileCharacterType").type(JsonFieldType.STRING)
                                        .description("게시자 프로필 이미지 타입" + getEnumValuesAsString(ProfileCharacter.class)),
                                fieldWithPath("data[].song.id").type(JsonFieldType.NUMBER)
                                        .description("곡 Id"),
                                fieldWithPath("data[].song.name").type(JsonFieldType.STRING)
                                        .description("곡 제목"),
                                fieldWithPath("data[].song.imageUrl").type(JsonFieldType.STRING)
                                        .description("곡 이미지 url"),
                                fieldWithPath("data[].song.artist.id").type(JsonFieldType.NUMBER)
                                        .description("곡 아티스트의 id"),
                                fieldWithPath("data[].song.artist.name").type(JsonFieldType.STRING)
                                        .description("곡 아티스트의 이름"),
                                fieldWithPath("data[].song.artist.imageUrl").type(JsonFieldType.STRING)
                                        .description("곡 아티스트의 이미지 url"),
                                fieldWithPath("data[].commentsCount").type(JsonFieldType.NUMBER)
                                        .description("댓글 개수"),
                                fieldWithPath("data[].likesCount").type(JsonFieldType.NUMBER)
                                        .description("좋아요 개수"),
                                fieldWithPath("data[].isLiked").type(JsonFieldType.BOOLEAN)
                                        .description("사용자의 해당 게시물 좋아요 여부"),
                                fieldWithPath("data[].isBookmarked").type(JsonFieldType.BOOLEAN)
                                        .description("사용자의 해당 게시물 북마크 여부")
                        )
                        .responseSchema(Schema.schema("Artist's Note List Response"))
                        .build()
                )
        );
    }
}