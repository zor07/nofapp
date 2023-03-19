package com.zor07.nofapp.api.v1.dto.userprogress.mapper;

import com.zor07.nofapp.api.v1.dto.userprogress.UserProgressDto;
import com.zor07.nofapp.entity.userprogress.UserProgress;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserProgressMapper {

    public UserProgressDto toDto(final List<UserProgress> userProgressList) {
        final var userTasks = userProgressList.stream().map(this::toUserTaskDto).toList();
        final var uncompletedTask = userProgressList.stream()
                .filter(userProgress -> userProgress.getCompletedDatetime() == null)
                .findFirst()
                .map(this::toUserTaskDto)
                .orElse(null);
        return new UserProgressDto(uncompletedTask, userTasks);
    }

    private UserProgressDto.UserTaskDto toUserTaskDto(final UserProgress userProgress) {
        return new UserProgressDto.UserTaskDto(userProgress.getTask(), userProgress.getCompletedDatetime() != null);
    }

}
