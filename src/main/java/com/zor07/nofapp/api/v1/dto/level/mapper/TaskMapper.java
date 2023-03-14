package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.api.v1.dto.level.LevelDto;
import com.zor07.nofapp.api.v1.dto.level.TaskDto;
import com.zor07.nofapp.entity.level.Level;
import com.zor07.nofapp.entity.level.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String fromJsonNode(final JsonNode jsonNode) {
        return jsonNode == null ? null : jsonNode.toString();
    }

    private JsonNode fromString(final String string) throws JsonProcessingException {
        return string == null ? null : OBJECT_MAPPER.readTree(string);
    }
    private String getFileUri(final Task task) {
        final var avatar = task.getFile();
        if (avatar == null) {
            return null;
        }
        return String.format("%s/%s", avatar.getBucket(), avatar.getKey());
    }

    private LevelDto levelToLevelDto(final Level level) {
        if (level == null) {
            return null;
        }
        final var id = level.getId();
        final var name = level.getName();
        final var order = level.getOrder();
        return new LevelDto(
                id,
                name,
                order,
                null
        );
    }

    private Level levelDtoToLevel(final LevelDto dto) {
        if (dto == null) {
            return null;
        }
        final var level = new Level();
        level.setId(dto.id());
        level.setName(dto.name());
        level.setOrder(dto.order());
        return level;
    }

    public TaskDto toDto(final Task entity) throws JsonProcessingException {
        final var id = entity.getId();
        final var name = entity.getName();
        final var description = entity.getDescription();
        final var order = entity.getOrder();
        final var level = levelToLevelDto(entity.getLevel());
        final var fileUri = getFileUri(entity);
        final var data = fromString(entity.getData());
        return new TaskDto(
                id,
                name,
                description,
                order,
                level,
                fileUri,
                data
        );

    }

    public Task toEntity(final TaskDto dto) {
        final var task = new Task();
        task.setId(dto.id());
        task.setLevel(levelDtoToLevel(dto.level()));
        task.setOrder(dto.order());
        task.setName(dto.name());
        task.setDescription(dto.description());
        task.setData(fromJsonNode(dto.data()));
        return task;
    }


}
