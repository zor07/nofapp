package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.TimerDto;
import com.zor07.nofapp.timer.TimerRepository;
import com.zor07.nofapp.timer.TimerService;
import com.zor07.nofapp.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/timer")
public class TimerController {

  private final UserService userService;

  private final TimerRepository repository;

  private final TimerService timerService;

  @Autowired
  public TimerController(final UserService userService,
                         final TimerRepository repository,
                         final TimerService timerService) {
    this.userService = userService;
    this.repository = repository;
    this.timerService = timerService;
  }

  @GetMapping(produces = "application/json")
  public List<TimerDto> findAll(final Principal principal) {
    final var user = userService.getUser(principal);
    return repository.findAllByUserId(user.getId())
        .stream()
        .map(TimerDto::toDto)
        .collect(Collectors.toList());
  }

  @PostMapping(consumes = "application/json")
  public ResponseEntity<Void> save(@RequestBody final TimerDto timer, final Principal principal) {
    final var user = userService.getUser(principal);
    timerService.save(TimerDto.toEntity(timer, user));
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PutMapping(path = "/{timerId}/stop")
  public ResponseEntity<Void> stop(@PathVariable final Long timerId, final Principal principal) {
    final var user = userService.getUser(principal);
    timerService.stopTimer(timerId, user.getId());
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @DeleteMapping("/{timerId}")
  public ResponseEntity<Void> delete(@PathVariable final Long timerId, final Principal principal) {
    final var user = userService.getUser(principal);
    timerService.deleteByIdAndUserId(timerId, user.getId());
    return ResponseEntity.noContent().build();
  }
}
