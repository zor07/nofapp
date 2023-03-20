package com.zor07.nofapp.api.v1.dto.userprogress.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zor07.nofapp.api.v1.dto.level.mapper.TaskMapper;
import com.zor07.nofapp.api.v1.dto.userprogress.UserProgressDto;
import com.zor07.nofapp.entity.userprogress.UserProgress;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserProgressMapper {

    private final TaskMapper taskMapper;

    public UserProgressMapper(final TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    public UserProgressDto toDto(final List<UserProgress> userProgressList) {
        final var userTasks = userProgressList.stream().map(this::toUserTaskDto).toList();
        final var uncompletedTask = userProgressList.stream()
                .filter(userProgress -> userProgress.getCompletedDatetime() == null)
                .findFirst()
                .map(this::toUserTaskDto)
                .orElse(null);
        return new UserProgressDto(uncompletedTask, userTasks);
    }

    private UserProgressDto.UserTaskDto toUserTaskDto(final UserProgress userProgress)  {
        try {
            return new UserProgressDto.UserTaskDto(taskMapper.toDto(userProgress.getTask()), userProgress.getCompletedDatetime() != null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
