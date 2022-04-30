package com.zor07.nofapp.api.v1.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.zor07.nofapp.entity.Timer;
import com.zor07.nofapp.entity.User;

public class TimerDto {

  public static TimerDto toDto(final Timer entity) {
    final var timer = new TimerDto();
    timer.id = entity.getId();
    timer.isRunning = entity.getStop() == null;
    timer.start = LocalDateTime.ofInstant(entity.getStart(), ZoneId.systemDefault());
    timer.stop = entity.getStop() == null ? null : LocalDateTime.ofInstant(entity.getStop(), ZoneId.systemDefault());;
    timer.description = entity.getDescription();
    return timer;
  }

  public static Timer toEntity(final TimerDto dto, User user) {
    final var timer = new Timer();
    timer.setId(dto.id);
    timer.setUser(user);
    timer.setStart(dto.start.atZone(ZoneId.systemDefault()).toInstant());
    timer.setStop(dto.stop == null ? null : dto.stop.atZone(ZoneId.systemDefault()).toInstant());
    timer.setDescription(dto.description);
    return timer;
  }

  public Long id;

  public boolean isRunning;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  public LocalDateTime start;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  public LocalDateTime stop;

  public String description;

}
