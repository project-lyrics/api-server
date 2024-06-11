package com.projectlyrics.server.common;

import com.projectlyrics.server.common.db.DatabaseCleanerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(DatabaseCleanerExtension.class)
public abstract class IntegrationTest {
}
