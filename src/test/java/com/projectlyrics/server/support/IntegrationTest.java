package com.projectlyrics.server.support;

import com.projectlyrics.server.support.db.DatabaseCleanerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(DatabaseCleanerExtension.class)
public abstract class IntegrationTest {
}
