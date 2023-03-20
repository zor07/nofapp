package com.zor07.nofapp.api.v1.dto.userprogress;

import com.zor07.nofapp.api.v1.dto.level.TaskDto;

import java.util.List;

public record UserProgressDto(UserTaskDto uncompletedTask, List<UserTaskDto> userTasks) {


    public record UserTaskDto(TaskDto task, boolean completed) {
    }
}
