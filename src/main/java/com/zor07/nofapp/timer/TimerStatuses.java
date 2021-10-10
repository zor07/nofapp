package com.zor07.nofapp.timer;

import java.util.List;

public record TimerStatuses(List<String> statuses) {
  public List<String> getStatuses() {
    return statuses;
  }
}
