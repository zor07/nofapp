package com.zor07.nofapp.api.v1.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class TimerDto {

  public Long id;

  public boolean isRunning;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  public LocalDateTime start;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  public LocalDateTime stop;

  public String description;

}
