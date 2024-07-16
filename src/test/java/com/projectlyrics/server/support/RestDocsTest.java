package com.projectlyrics.server.support;

import com.projectlyrics.server.support.config.RestDocsConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
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

    protected static FieldDescriptor[] getErrorResponseFields() {
        return new FieldDescriptor[]{
                fieldWithPath("errorCode").type(JsonFieldType.STRING)
                        .description("에러코드"),
                fieldWithPath("errorMessage").type(JsonFieldType.STRING)
                        .description("메세지")
        };
    }

    protected <E extends Enum<E>> String getEnumValuesAsString(Class<E> enumClass) {
        String enumValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        return " (종류: " + enumValues + ")";
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
