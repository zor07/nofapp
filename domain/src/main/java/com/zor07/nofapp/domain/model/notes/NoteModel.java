package com.zor07.nofapp.domain.model.notes;

import com.zor07.nofapp.domain.validation.JsonString;


public record NoteModel(
        Long id,
        NotebookModel notebook,
        String title,
        @JsonString String data
) {

}
