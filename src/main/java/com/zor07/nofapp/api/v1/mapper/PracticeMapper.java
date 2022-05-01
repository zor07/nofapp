package com.zor07.nofapp.api.v1.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.api.v1.dto.PracticeDto;
import com.zor07.nofapp.api.v1.dto.PracticeTagDto;
import com.zor07.nofapp.entity.Practice;
import com.zor07.nofapp.entity.PracticeTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PracticeMapper {

    default String fromJsonNode(JsonNode jsonNode) {
        return jsonNode == null ? null : jsonNode.toString();
    }

    default JsonNode fromString(String string) throws JsonProcessingException {
        return string == null ? null : new ObjectMapper().readTree(string);
    }

    PracticeTagDto toPracticeTagDto(final PracticeTag entity);
    PracticeTag toPracticeTagEntity(final PracticeDto dto);


    @Mapping(target = "isPublic", source = "public")
    PracticeDto toDto(final Practice entity);

    @Mapping(target = "public", source = "isPublic")
    Practice toEntity(final PracticeDto dto);

}
