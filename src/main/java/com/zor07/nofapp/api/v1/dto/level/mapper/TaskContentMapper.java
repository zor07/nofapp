package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.api.v1.dto.level.TaskContentDto;
import com.zor07.nofapp.entity.level.TaskContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TaskContentMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    default String fromJsonNode(final JsonNode jsonNode) {
        return jsonNode == null ? null : jsonNode.toString();
    }

    default JsonNode fromString(final String string) throws JsonProcessingException {
        return string == null ? null : OBJECT_MAPPER.readTree(string);
    }
    default String getFileUri(final TaskContent taskContent) {
        final var avatar = taskContent.getFile();
        if (avatar == null) {
            return null;
        }
        return String.format("%s/%s", avatar.getBucket(), avatar.getKey());
    }

    @Mappings({
            @Mapping(target = "fileUri", expression = "java(getFileUri(entity))"),
            @Mapping(target = "data", expression = "java(fromString(entity.getData()))"),
    })
    TaskContentDto toDto(final TaskContent entity) throws JsonProcessingException;


    @Mappings({
            @Mapping(target = "id", expression = "java(dto.id())"),
            @Mapping(target = "title", expression = "java(dto.title())"),
            @Mapping(target = "data", expression = "java(fromJsonNode(dto.data()))"),
            @Mapping(target = "file", ignore = true),
    })
    TaskContent toEntity(final TaskContentDto dto);

}
