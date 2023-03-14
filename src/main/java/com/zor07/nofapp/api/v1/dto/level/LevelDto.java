package com.zor07.nofapp.api.v1.dto.level;

import java.util.List;

public record LevelDto(
        Long id,
        String name,
        Integer order,
        List<TaskDto> tasks
) {

}
