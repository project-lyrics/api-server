package com.projectlyrics.server.domain.note.api;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.dto.request.NoteUpdateRequest;
import com.projectlyrics.server.domain.note.dto.response.NoteGetResponse;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.support.RestDocsTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.NoteFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
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
                .andDo(getCreateNoteDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getCreateNoteDocument() {
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
        mockMvc.perform(patch("/api/v1/notes/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(noteUpdateRequest)))
                .andDo(getUpdateNoteDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getUpdateNoteDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Note API")
                        .summary("노트 수정 API")
                        .requestHeaders(getAuthorizationHeader())
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
            data.add(NoteGetResponse.of(note, LocalDateTime.now()));
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
            data.add(NoteGetResponse.of(note, LocalDateTime.now()));
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
                                        .description("곡 아티스트의 이미지 url")
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
            data.add(NoteGetResponse.of(note, LocalDateTime.now()));
        }

        CursorBasePaginatedResponse<NoteGetResponse> response = new CursorBasePaginatedResponse<>(
                data.get(data.size() - 1).getId(),
                true,
                data
        );

        given(noteQueryService.getNotesByArtistId(any(), anyBoolean(), any(), anyInt()))
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
                        .summary("노트 리스트 조회 API")
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
                                        .description("곡 아티스트의 이미지 url")
                        )
                        .responseSchema(Schema.schema("Artist's Note List Response"))
                        .build()
                )
        );
    }
}