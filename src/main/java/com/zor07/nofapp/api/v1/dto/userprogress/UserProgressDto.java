package com.zor07.nofapp.api.v1.dto.userprogress;

import com.zor07.nofapp.entity.level.Task;

import java.util.List;

public record UserProgressDto(UserTaskDto uncompletedTask, List<UserTaskDto> userTasks) {


    public record UserTaskDto(Task task, boolean completed) {
    }
}
