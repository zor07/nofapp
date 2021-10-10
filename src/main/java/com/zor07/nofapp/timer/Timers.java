package com.zor07.nofapp.timer;

import java.util.List;

public record Timers(List<Timer> timers) {

  @Override
  public List<Timer> timers() {
    return timers;
  }
}
