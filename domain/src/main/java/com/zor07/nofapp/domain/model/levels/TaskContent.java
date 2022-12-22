package com.zor07.nofapp.domain.model.levels;

import com.zor07.nofapp.domain.model.file.File;
import com.zor07.nofapp.domain.validation.JsonString;

public record TaskContent(
        Long id,
        File file,
        String title,
        @JsonString String data
) {

}
