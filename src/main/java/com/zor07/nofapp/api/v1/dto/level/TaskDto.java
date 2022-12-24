package com.zor07.nofapp.api.v1.dto.level;


public record TaskDto(
        Long id,
        String name,
        String description,
        Integer order,
        LevelDto level,
        TaskContentDto taskContent
) {

}
