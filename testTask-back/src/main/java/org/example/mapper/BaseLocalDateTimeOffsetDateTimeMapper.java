package org.example.mapper;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class BaseLocalDateTimeOffsetDateTimeMapper {
    public OffsetDateTime map(LocalDateTime localDateTime) {
        return localDateTime.atOffset(ZoneOffset.UTC);
    }

    public LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toLocalDateTime();
    }
}
