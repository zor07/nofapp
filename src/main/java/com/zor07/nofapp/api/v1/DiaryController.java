package com.zor07.nofapp.api.v1;

import java.security.Principal;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zor07.nofapp.api.v1.dto.DiaryDto;
import com.zor07.nofapp.diary.DiaryRepository;
import com.zor07.nofapp.user.User;
import com.zor07.nofapp.user.UserService;

@RestController
@RequestMapping("/api/v1/diary")
public class DiaryController {

  private final UserService userService;

  private final DiaryRepository repository;

  @Autowired
  public DiaryController(UserService userService, final DiaryRepository repository) {
    this.userService = userService;
    this.repository = repository;
  }

  @GetMapping(path = "/{diaryId}", produces = "application/json")
  public DiaryDto findById(@PathVariable final Long diaryId, final Principal principal) {
    final var user = getUser(principal);
    return DiaryDto.toDto(repository.findByIdAndUserId(diaryId, user.getId()));
  }

  @GetMapping(produces = "application/json")
  public List<DiaryDto> findAll(final Principal principal) {
    final var user = getUser(principal);
    return repository.findAllByUserId(user.getId())
        .stream()
        .map(DiaryDto::toDto)
        .collect(Collectors.toList());
  }

  @PostMapping(consumes = "application/json")
  @Transactional
  public ResponseEntity<Void> save(@RequestBody final DiaryDto diary, final Principal principal) {
    final var user = getUser(principal);
    repository.save(DiaryDto.toEntity(diary, user));
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @DeleteMapping("/{diaryId}")
  @Transactional
  public ResponseEntity<Void> delete(@PathVariable final Long diaryId, final Principal principal) {
    final var user = getUser(principal);
    try {
      repository.deleteByIdAndUserId(diaryId, user.getId());
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