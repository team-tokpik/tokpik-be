package org.example.tokpik_be.util;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class TimeProvider {

    public LocalDateTime provideSystemTime() {

        return LocalDateTime.now();
    }
}
