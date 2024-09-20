package org.example.tokpik_be.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.tokpik_be.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public abstract class ControllerTestSupport {

    protected final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());
    protected MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    protected abstract Object initController();
}
