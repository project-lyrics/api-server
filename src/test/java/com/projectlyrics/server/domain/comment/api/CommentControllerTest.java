package com.projectlyrics.server.domain.comment.api;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.projectlyrics.server.domain.comment.dto.request.CommentCreateRequest;
import com.projectlyrics.server.domain.comment.dto.request.CommentUpdateRequest;
import com.projectlyrics.server.support.RestDocsTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends RestDocsTest {

    @Test
    void 댓글을_저장하면_200응답을_해야_한다() throws Exception {
        // given
        CommentCreateRequest request = new CommentCreateRequest(
                "content",
                1L
        );

        // when, then
        mockMvc.perform(post("/api/v1/comments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(getCreateCommentDocument());
    }

    private RestDocumentationResultHandler getCreateCommentDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Comment API")
                        .summary("댓글 등록 API")
                        .requestHeaders(getAuthorizationHeader())
                        .requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("댓글 내용"),
                                fieldWithPath("noteId").type(JsonFieldType.NUMBER)
                                        .description("노트 ID")
                        )
                        .requestSchema(Schema.schema("Create Comment Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Create Comment Response"))
                        .build())
        );
    }

    @Test
    void 댓글을_수정하면_200응답을_해야_한다() throws Exception {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest(
                "content"
        );

        // when, then
        mockMvc.perform(patch("/api/v1/comments/{commentId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(getUpdateCommentDocument());
    }

    private RestDocumentationResultHandler getUpdateCommentDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Comment API")
                        .summary("댓글 수정 API")
                        .requestHeaders(getAuthorizationHeader())
                        .pathParameters(
                                parameterWithName("commentId").description("댓글 ID")
                        )
                        .requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("새로운 댓글 내용")
                        )
                        .requestSchema(Schema.schema("Update Comment Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Update Comment Response"))
                        .build())
        );
    }

    @Test
    void 댓글을_삭제하면_200응답을_해야_한다() throws Exception {
        // when, then
        mockMvc.perform(delete("/api/v1/comments/{commentId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(getDeleteCommentDocument());
    }

    private RestDocumentationResultHandler getDeleteCommentDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Comment API")
                        .summary("댓글 삭제 API")
                        .requestHeaders(getAuthorizationHeader())
                        .pathParameters(
                                parameterWithName("commentId").description("댓글 ID")
                        )
                        .requestSchema(Schema.schema("Delete Comment Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Delete Comment Response"))
                        .build())
        );
    }
}