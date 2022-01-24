package com.zor07.nofapp.api.v1.dto;

import com.zor07.nofapp.diary.Diary;
import com.zor07.nofapp.user.User;

public class DiaryDto {

  public static DiaryDto toDto(final Diary entity) {
    final var diary = new DiaryDto();
    diary.id = entity.getId();
    diary.title = entity.getTitle();
    diary.data = entity.getData();
    return diary;
  }

  public static Diary toEntity(final DiaryDto dto, User user) {
    final var diary = new Diary();
    diary.setId(dto.id);
    diary.setUser(user);
    diary.setTitle(dto.title);
    diary.setData(dto.data);
    return diary;
  }

  public Long id;
  public String title;
  public String data;

}
