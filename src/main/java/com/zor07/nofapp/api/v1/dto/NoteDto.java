package com.zor07.nofapp.api.v1.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.api.v1.mapper.NotebookMapper;
import com.zor07.nofapp.entity.Note;
import com.zor07.nofapp.entity.NoteIdAndTitle;
import com.zor07.nofapp.entity.User;
import org.mapstruct.factory.Mappers;

public class NoteDto {

  private static final NotebookMapper notebookMapper = Mappers.getMapper(NotebookMapper.class);

  public static NoteDto toDto(final NoteIdAndTitle entity) {
    final var note = new NoteDto();
    note.id = entity.getId();
    note.title = entity.getTitle();
    return note;
  }

  public static NoteDto toDto(final Note note) throws JsonProcessingException {
    final var dto = new NoteDto();
    dto.id = note.getId();
    dto.title = note.getTitle();
    dto.notebookDto = notebookMapper.toDto(note.getNotebook());
    dto.data =  new ObjectMapper().readTree(note.getData());
    return dto;
  }

  public static Note toEntity(final NoteDto dto, final User user) {
    final var note = new Note();
    note.setId(dto.id);
    note.setNotebook(notebookMapper.toEntity(dto.notebookDto, user));
    note.setTitle(dto.title);
    note.setData(dto.data.toString());
    return note;
  }

  public Long id;
  public String title;
  public NotebookDto notebookDto;
  public JsonNode data;

}
