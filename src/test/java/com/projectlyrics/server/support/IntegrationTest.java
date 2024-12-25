package com.projectlyrics.server.support;

import com.projectlyrics.server.support.db.DatabaseCleanerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@ExtendWith(DatabaseCleanerExtension.class)
public abstract class IntegrationTest {

    @MockBean
    protected OpenSearchClient openSearchClient;
}
