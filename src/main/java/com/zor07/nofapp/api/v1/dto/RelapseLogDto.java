package com.zor07.nofapp.api.v1.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public record RelapseLogDto(
        Long id,
        @JsonSerialize(using = LocalDateTimeSerializer.class) LocalDateTime start,
        @JsonSerialize(using = LocalDateTimeSerializer.class) LocalDateTime stop
) {

}
