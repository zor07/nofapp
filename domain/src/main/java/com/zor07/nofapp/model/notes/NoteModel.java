package com.zor07.nofapp.model.notes;

import com.zor07.nofapp.validation.JsonString;


public record NoteModel(
        Long id,
        NotebookModel notebook,
        String title,
        @JsonString String data
) {

}
