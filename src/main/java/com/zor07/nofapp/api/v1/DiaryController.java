package com.zor07.nofapp.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zor07.nofapp.api.v1.dto.DiaryDto;
import com.zor07.nofapp.repository.DiaryRepository;
import com.zor07.nofapp.service.UserService;
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

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/diary")
@Deprecated //soon will be replaced with Note
public class DiaryController {

  private final UserService userService;

  private final DiaryRepository repository;

  @Autowired
  public DiaryController(UserService userService, final DiaryRepository repository) {
    this.userService = userService;
    this.repository = repository;
  }

  @GetMapping(path = "/{diaryId}", produces = "application/json")
  public DiaryDto findById(@PathVariable final Long diaryId, final Principal principal) throws JsonProcessingException {
    final var user = userService.getUser(principal);
    return DiaryDto.toDto(repository.findByIdAndUserId(diaryId, user.getId()));
  }

  @GetMapping(produces = "application/json")
  public List<DiaryDto> findAll(final Principal principal) {
    final var user = userService.getUser(principal);
    return repository.findAllByUserId(user.getId())
        .stream()
        .map(DiaryDto::toDto)
        .collect(Collectors.toList());
  }

  @PostMapping(consumes = "application/json")
  @Transactional
  public ResponseEntity<DiaryDto> save(@RequestBody final DiaryDto diary, final Principal principal) throws JsonProcessingException {
    final var user = userService.getUser(principal);
    return new ResponseEntity<>(
        DiaryDto.toDto(repository.save(DiaryDto.toEntity(diary, user))),
        HttpStatus.CREATED
    );
  }

  @DeleteMapping("/{diaryId}")
  @Transactional
  public ResponseEntity<Void> delete(@PathVariable final Long diaryId, final Principal principal) {
    final var user = userService.getUser(principal);
    try {
      repository.deleteByIdAndUserId(diaryId, user.getId());
    } catch (EmptyResultDataAccessException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
