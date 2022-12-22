package com.zor07.nofapp.model.levels;


public record TaskModel(
        Long id,
        LevelModel level,
        TaskContentModel taskContent,
        Integer order,
        String name,
        String description
) {

}
