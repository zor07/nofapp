package com.zor07.nofapp.api.v1.dto.practice;

import com.fasterxml.jackson.databind.JsonNode;

public record PracticeDto(
        Long id,
        PracticeTagDto practiceTag,
        String name,
        String description,
        JsonNode data,
        boolean isPublic
) {

}
