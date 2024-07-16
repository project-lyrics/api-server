package com.projectlyrics.server.support.config;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@TestConfiguration
public class RestDocsConfiguration {

    @Bean
    public RestDocumentationResultHandler restDocumentationResultHandler() {
        return MockMvcRestDocumentationWrapper.document(
                "{class-name}/{method-name}",
                preprocessRequest(
                        modifyHeaders()
                                .remove("Content-Length")
                                .remove("Host"),
                        prettyPrint()),
                preprocessResponse(
                        modifyHeaders()
                                .remove("Content-Length")
                                .remove("X-Content-Type-Options")
                                .remove("X-XSS-Protection")
                                .remove("Cache-Control")
                                .remove("Pragma")
                                .remove("Expires")
                                .remove("X-Frame-Options"),
                        prettyPrint())
        );
    }
}
