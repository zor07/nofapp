package com.zor07.nofapp.api.v1.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record NoteDto(
        Long id,
        String title,
        NotebookDto notebookDto,
        JsonNode data
) {

}
