package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.api.v1.dto.level.TaskDto;
import com.zor07.nofapp.entity.level.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    default String fromJsonNode(final JsonNode jsonNode) {
        return jsonNode == null ? null : jsonNode.toString();
    }

    default JsonNode fromString(final String string) throws JsonProcessingException {
        return string == null ? null : OBJECT_MAPPER.readTree(string);
    }
    default String getFileUri(final Task task) {
        final var avatar = task.getFile();
        if (avatar == null) {
            return null;
        }
        return String.format("%s/%s", avatar.getBucket(), avatar.getKey());
    }

    @Mappings({
            @Mapping(target = "fileUri", expression = "java(getFileUri(entity))"),
            @Mapping(target = "data", expression = "java(fromString(entity.getData()))"),
    })
    TaskDto toDto(final Task entity) throws JsonProcessingException;

    @Mappings({
            @Mapping(target = "id", expression = "java(dto.id())"),
            @Mapping(target = "name", expression = "java(dto.name())"),
            @Mapping(target = "description", expression = "java(dto.description())"),
            @Mapping(target = "order", expression = "java(dto.order())"),
            @Mapping(target = "level", source = "level"),
            @Mapping(target = "data", expression = "java(fromJsonNode(dto.data()))"),
            @Mapping(target = "file", ignore = true),
    })
    Task toEntity(final TaskDto dto);

}
