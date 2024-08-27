package com.projectlyrics.server.support;

import com.epages.restdocs.apispec.HeaderDescriptorWithType;
import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.SimpleType;
import com.projectlyrics.server.support.config.RestDocsConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.Attribute;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTest extends ControllerTest {

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    protected static final String ERROR_RESPONSE_SCHEMA = "Error Response";

    protected static Attribute constraints(final String value) {
        return new Attribute("constraint", value);
    }

    protected static HeaderDescriptorWithType getAuthorizationHeader() {
        return headerWithName(HttpHeaders.AUTHORIZATION).type(SimpleType.STRING)
                .description("Bearer ${accessToken}");
    }

    protected static FieldDescriptor[] getErrorResponseFields() {
        return new FieldDescriptor[]{
                fieldWithPath("errorCode").type(JsonFieldType.STRING)
                        .description("에러코드"),
                fieldWithPath("errorMessage").type(JsonFieldType.STRING)
                        .description("메세지")
        };
    }

    protected static ParameterDescriptorWithType[] getCursorBasePagingQueryParameters() {
        return new ParameterDescriptorWithType[]{
                parameterWithName("cursor").type(SimpleType.NUMBER)
                        .optional()
                        .description("이전에 응답 받은 nextCursor 값, 해당 cursor보다 큰 id를 가진 아티스트 조회"),
                parameterWithName("size").type(SimpleType.NUMBER)
                        .optional()
                        .description("데이터 수 (default = 10)")
        };
    }

    protected static ParameterDescriptorWithType[] getOffsetBasePagingQueryParameters() {
        return new ParameterDescriptorWithType[]{
                parameterWithName("pageNumber").type(SimpleType.NUMBER)
                        .optional()
                        .description("페이지 번호 (default = 0)"),
                parameterWithName("pageSize").type(SimpleType.NUMBER)
                        .optional()
                        .description("페이지 크기 (default = 10)")
        };
    }

    protected <E extends Enum<E>> String getEnumValuesAsString(Class<E> enumClass) {
        String enumValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        return " (enum: " + enumValues + ")";
    }

    @BeforeEach
    void setUp(final WebApplicationContext context,
               final RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(MockMvcResultHandlers.print())
//                .alwaysDo(restDocs)
                .build();
    }
}
