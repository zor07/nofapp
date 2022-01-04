package com.zor07.nofapp.api.v1;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zor07.nofapp.api.v1.dto.TimerDto;
import com.zor07.nofapp.timer.TimerRepository;

@RestController
@RequestMapping("/api/v1/timer")
public class TimerController {

  private final TimerRepository repository;

  @Autowired
  public TimerController(final TimerRepository repository) {
    this.repository = repository;
  }

  @GetMapping(produces = "application/json")
  public List<TimerDto> findAll() {
    return repository.findAll()
        .stream()
        .map(TimerDto::toDto)
        .collect(Collectors.toList());
  }

  @GetMapping(value = "/{timerId}", produces = "application/json")
  public ResponseEntity<TimerDto> findOne(@PathVariable final Long timerId) {
    return repository.findById(timerId)
        .map(timer -> new ResponseEntity<>(TimerDto.toDto(timer), HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping(consumes = "application/json")
  public ResponseEntity<Void> save(@RequestBody final TimerDto timer) {
    repository.save(TimerDto.toEntity(timer));
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @DeleteMapping("/{timerId}")
  public ResponseEntity<Void> delete(@PathVariable final Long timerId) {
    try {
      repository.deleteById(timerId);
    } catch (EmptyResultDataAccessException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
