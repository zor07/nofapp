package com.zor07.nofapp.domain.model.notes;

import com.zor07.nofapp.domain.validation.JsonString;


public record Note(
        Long id,
        Notebook notebook,
        String title,
        @JsonString String data
) {

}
