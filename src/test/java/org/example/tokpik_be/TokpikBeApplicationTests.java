package org.example.tokpik_be;

import org.example.tokpik_be.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestConfig.class)
@ActiveProfiles("test")
@SpringBootTest
class TokpikBeApplicationTests {

    @Test
    void contextLoads() {

    }
}


