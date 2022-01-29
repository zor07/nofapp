package com.zor07.nofapp.api.v1.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.nofapp.diary.Diary;
import com.zor07.nofapp.diary.IdAndTitleOnly;
import com.zor07.nofapp.user.User;

public class DiaryDto {

  public static DiaryDto toDto(final IdAndTitleOnly entity) {
    final var diary = new DiaryDto();
    diary.id = entity.getId();
    diary.title = entity.getTitle();
    return diary;
  }

  public static DiaryDto toDto(final Diary entity) throws JsonProcessingException {
    final var diary = new DiaryDto();
    diary.id = entity.getId();
    diary.title = entity.getTitle();
    diary.data =  new ObjectMapper().readTree(entity.getData());
    return diary;
  }

  public static Diary toEntity(final DiaryDto dto, User user) {
    final var diary = new Diary();
    diary.setId(dto.id);
    diary.setUser(user);
    diary.setTitle(dto.title);
    diary.setData(dto.data.toString());
    return diary;
  }

  public Long id;
  public String title;
  public JsonNode data;

}
