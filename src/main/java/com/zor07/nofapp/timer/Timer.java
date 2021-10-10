package com.zor07.nofapp.timer;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Timer {

  public Integer id;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  public LocalDateTime startPoint;
  public String description;
  public int daysGoal;
  public int daysStep;

}
