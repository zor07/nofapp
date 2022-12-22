package com.zor07.nofapp.domain.model.levels;


public record TaskModel(
        Long id,
        LevelModel level,
        TaskContentModel taskContent,
        Integer order,
        String name,
        String description
) {

}
