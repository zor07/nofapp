package com.zor07.nofapp.api.v1.dto.level.mapper;

import com.zor07.nofapp.api.v1.dto.level.LevelDto;
import com.zor07.nofapp.entity.level.Level;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface LevelMapper {

    LevelDto toDto(final Level entity);

    @Mappings({
            @Mapping(target = "id", expression = "java(dto.id())"),
            @Mapping(target = "name", expression = "java(dto.name())"),
            @Mapping(target = "order", expression = "java(dto.order())"),
    })
    Level toEntity(final LevelDto dto);

}
