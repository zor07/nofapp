package com.zor07.nofapp.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zor07.nofapp.timer.Timer;
import com.zor07.nofapp.timer.TimerService;
import com.zor07.nofapp.timer.TimerStatuses;

@RestController
@RequestMapping("/v1/timer")
public class TimerController {

  private final TimerService timerService;

  public TimerController(TimerService timerService) {
    this.timerService = timerService;
  }

  @GetMapping
  public TimerStatuses status() {
    return timerService.getStatuses();
  }

  @PostMapping
  public ResponseEntity<Void> createTimer(@RequestBody final Timer timer) {
    timerService.createTimer(timer);
    return ResponseEntity.ok(null);
  }

}
