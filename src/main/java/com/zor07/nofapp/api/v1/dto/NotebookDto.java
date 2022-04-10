package com.zor07.nofapp.api.v1.dto;

import com.zor07.nofapp.notebook.Notebook;
import com.zor07.nofapp.user.User;

public class NotebookDto {

  public static NotebookDto toDto(final Notebook entity) {
    final var dto = new NotebookDto();
    dto.id = entity.getId();
    dto.name = entity.getName();
    dto.description = entity.getDescription();
    return dto;
  }

  public static Notebook toEntity(final NotebookDto dto, User user) {
    final var entity = new Notebook();
    entity.setId(dto.id);
    entity.setUser(user);
    entity.setName(dto.name);
    entity.setDescription(dto.description);
    return entity;
  }

  public Long id;
  public String name;
  public String description;

}
