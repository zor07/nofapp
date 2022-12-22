package com.zor07.nofapp.domain.model.levels;

import com.zor07.nofapp.domain.model.file.FileModel;
import com.zor07.nofapp.domain.validation.JsonString;

public record TaskContentModel(
        Long id,
        FileModel file,
        String title,
        @JsonString String data
) {

}
