package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zor07.nofapp.api.v1.dto.level.LevelDto;
import com.zor07.nofapp.api.v1.dto.level.TaskDto;
import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.entity.level.Task;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class LevelMapper {

    private final TaskMapper taskMapper;

    public LevelMapper(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    public LevelDto toDto(Level entity) {
        final var id = entity.getId();
        final var name = entity.getName();
        final var order = entity.getOrder();
        final var tasks = taskListToTaskDtoList(entity.getTasks());
        return new LevelDto(
                id,
                name,
                order,
                tasks
        );
    }

    public Level toEntity(final LevelDto dto) {
        final var level = new Level();
        level.setId(dto.id());
        level.setName(dto.name());
        level.setOrder(dto.order());
        level.setTasks(taskDtoListToTaskList(dto.tasks()));
        return level;
    }

    private List<TaskDto> taskListToTaskDtoList(final List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return Collections.emptyList();
        return tasks.stream()
                .map(task -> {
                    try {
                        return taskMapper.toDto(task);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    private List<Task> taskDtoListToTaskList(final List<TaskDto> tasks) {
        if (tasks == null || tasks.isEmpty()) return Collections.emptyList();
        return tasks.stream().map(taskMapper::toEntity).toList();
    }

}
