package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.zor07.nofapp.api.v1.dto.level.TaskDto;
import com.zor07.nofapp.entity.level.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDto toDto(final Task entity);

    @Mappings({
            @Mapping(target = "id", expression = "java(dto.id())"),
            @Mapping(target = "name", expression = "java(dto.name())"),
            @Mapping(target = "description", expression = "java(dto.description())"),
            @Mapping(target = "order", expression = "java(dto.order())"),
            @Mapping(target = "level", source = "level"),
    })
    Task toEntity(final TaskDto dto);

}
