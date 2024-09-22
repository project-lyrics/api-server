package com.projectlyrics.server.domain.report.api;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.report.domain.ReportReason;
import com.projectlyrics.server.domain.report.dto.request.ReportCreateRequest;
import com.projectlyrics.server.support.RestDocsTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

public class ReportControllerTest extends RestDocsTest {

    @Test
    void 신고를_저장하면_200응답을_해야_한다() throws Exception {
        // given
        ReportCreateRequest request = new ReportCreateRequest(
                1L,
                null,
                ReportReason.POLITICAL_RELIGIOUS,
                null,
                "example@example.com"
        );

        // when, then
        mockMvc.perform(post("/api/v1/reports")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(getNoteCreateDocument())
                .andExpect(status().isOk());
    }

    private RestDocumentationResultHandler getNoteCreateDocument() {
        return restDocs.document(
                resource(ResourceSnippetParameters.builder()
                        .tag("Report API")
                        .summary("신고 등록 API")
                        .requestHeaders(getAuthorizationHeader())
                        .requestFields(
                                fieldWithPath("noteId").type(JsonFieldType.NUMBER)
                                        .description("신고할 noteId (noteId와 commentId 중 택 1)")
                                        .optional(),
                                fieldWithPath("commentId").type(JsonFieldType.STRING)
                                        .description("신고할 commentId (noteId와 commentId 중 택 1)")
                                        .optional(),
                                fieldWithPath("reportReason").type(JsonFieldType.STRING)
                                        .description("신고 이유 " + getEnumValuesAsString(ReportReason.class)),
                                fieldWithPath("detailedReportReason").type(JsonFieldType.STRING)
                                        .description("상세 신고 이유 (신고 이유가 기타일 때만 적용)" )
                                        .optional(),
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("신고자 이메일")
                                        .optional()
                        )
                        .requestSchema(Schema.schema("Create Report Request"))
                        .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부")
                        )
                        .responseSchema(Schema.schema("Create Report Response"))
                        .build())
        );
    }


}
