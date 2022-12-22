package com.zor07.nofapp.domain.model.levels;


public record Task(
        Long id,
        Level level,
        TaskContent taskContent,
        Integer order,
        String name,
        String description
) {

}
