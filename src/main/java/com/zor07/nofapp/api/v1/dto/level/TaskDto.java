package com.zor07.nofapp.api.v1.dto.level;


import com.fasterxml.jackson.databind.JsonNode;

public record TaskDto(
        Long id,
        String name,
        String description,
        Integer order,
        LevelDto level,
        String fileUri,
        JsonNode data
) {

}
