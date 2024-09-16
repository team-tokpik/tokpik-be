package org.example.tokpik_be.common;

import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

@Getter
@MappedSuperclass
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdAt;
}
