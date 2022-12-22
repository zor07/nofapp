package com.zor07.nofapp.model.levels;

import com.zor07.nofapp.model.file.FileModel;
import com.zor07.nofapp.validation.JsonString;

public record TaskContentModel(
        Long id,
        FileModel file,
        String title,
        @JsonString String data
) {

}
