package com.zor07.nofapp.api.v1.dto.notes.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.api.v1.dto.notes.NoteDto;
import com.zor07.nofapp.entity.notes.Note;
import com.zor07.nofapp.entity.notes.NoteIdAndTitle;
import com.zor07.nofapp.entity.user.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    default String fromJsonNode(final JsonNode jsonNode) {
        return jsonNode == null ? null : jsonNode.toString();
    }

    default JsonNode fromString(final String string) throws JsonProcessingException {
        return string == null ? null : OBJECT_MAPPER.readTree(string);
    }

    @Mapping(target = "notebookDto", source = "notebook")
    NoteDto toDto(final Note entity);

    @Mappings({
            @Mapping(target = "notebook", source = "notebookDto"),
            @Mapping(target = "notebook.user", expression = "java(user)"),
            @Mapping(target = "data", expression = "java(dto.data().toString())"),
            @Mapping(target = "id", expression = "java(dto.id())"),
            @Mapping(target = "title", expression = "java(dto.title())")
    })
    Note toEntity(final NoteDto dto, final @Context User user);

    @Mapping(target = "notebookDto", ignore = true)
    @Mapping(target = "data", ignore = true)
    NoteDto toDto(final NoteIdAndTitle entity);

}
