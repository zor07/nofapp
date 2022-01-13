package com.zor07.nofapp.api.v1;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
import com.zor07.nofapp.api.v1.dto.TimerDto;
import com.zor07.nofapp.timer.TimerRepository;
import com.zor07.nofapp.user.User;
import com.zor07.nofapp.user.UserService;

@RestController
@RequestMapping("/api/v1/timer")
public class TimerController {

  private final UserService userService;

  private final TimerRepository repository;

  @Autowired
  public TimerController(UserService userService, final TimerRepository repository) {
    this.userService = userService;
    this.repository = repository;
  }

  @GetMapping(produces = "application/json")
  public List<TimerDto> findAll(final Principal principal) {
    final var user = getUser(principal);
    return repository.findAllByUserId(user.getId())
        .stream()
        .map(TimerDto::toDto)
        .collect(Collectors.toList());
  }

  @PostMapping(consumes = "application/json")
  @Transactional
  public ResponseEntity<Void> save(@RequestBody final TimerDto timer, final Principal principal) {
    final var user = getUser(principal);
    repository.save(TimerDto.toEntity(timer, user));
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PutMapping(path = "/{timerId}/stop")
  @Transactional
  // TODO accept stop time from client
  public ResponseEntity<Void> stop(@PathVariable final Long timerId, final Principal principal) {
    final var user = getUser(principal);
    try {
      final var timer = repository.findByIdAndUserId(timerId, user.getId());
      timer.setStop(Instant.now());
      repository.save(timer);
    } catch (EmptyResultDataAccessException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @DeleteMapping("/{timerId}")
  @Transactional
  public ResponseEntity<Void> delete(@PathVariable final Long timerId, final Principal principal) {
    final var user = getUser(principal);
    try {
      repository.deleteByIdAndUserId(timerId, user.getId());
    } catch (EmptyResultDataAccessException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  private User getUser(final Principal principal) {
    final var username = principal.getName();
    return userService.getUser(username);
  }

}
