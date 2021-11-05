package com.zor07.nofapp.api.v1.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zor07.nofapp.timer.Timer;

public class TimerDto {

  public static TimerDto toDto(final Timer entity) {
    final var timer = new TimerDto();
    timer.id = entity.getId();
    timer.start = LocalDateTime.ofInstant(entity.getStart(), ZoneId.systemDefault());
    timer.stop = entity.getStop() == null ? null : LocalDateTime.ofInstant(entity.getStop(), ZoneId.systemDefault());;
    timer.description = entity.getDescription();
    return timer;
  }

  public static Timer toEntity(final TimerDto dto) {
    final var timer = new Timer();
    timer.setId(dto.id);
    timer.setStart(dto.start.atZone(ZoneId.systemDefault()).toInstant());
    timer.setStop(dto.stop == null ? null : dto.stop.atZone(ZoneId.systemDefault()).toInstant());
    timer.setDescription(dto.description);
    return timer;
  }

  public Long id;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  public LocalDateTime start;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  public LocalDateTime stop;

  public String description;

}
