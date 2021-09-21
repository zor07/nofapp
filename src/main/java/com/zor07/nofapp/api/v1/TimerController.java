package com.zor07.nofapp.api.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zor07.nofapp.services.TimerService;

@RestController
public class TimerController {

  private final TimerService timerService;

  public TimerController(TimerService timerService) {
    this.timerService = timerService;
  }

  @GetMapping("/v1/status")
  public Status status() {
    final var status = new Status();
    status.status = timerService.getStatus();
    return status;
  }

  static class Status {
    String status;

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }
  }

}
