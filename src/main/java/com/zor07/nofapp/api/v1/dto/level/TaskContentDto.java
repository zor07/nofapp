package com.zor07.nofapp.api.v1.dto.level;

import com.fasterxml.jackson.databind.JsonNode;

public record TaskContentDto(
        Long id,
        String title,
        String fileUri,
        JsonNode data
) {

}
