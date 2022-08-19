package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.TimerDto;
import com.zor07.nofapp.api.v1.mapper.TimerMapper;
import com.zor07.nofapp.service.TimerService;
import com.zor07.nofapp.service.UserService;
import com.zor07.nofapp.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/timers")
@Api(tags = "Timers")
public class TimerController {

  private final UserService userService;
  private final TimerService timerService;
  private final TimerMapper timerMapper;

  @Autowired
  public TimerController(final UserService userService,
                         final TimerService timerService,
                         final TimerMapper timerMapper) {
    this.userService = userService;
    this.timerService = timerService;
    this.timerMapper = timerMapper;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Retrieves all timers of current user", response = TimerDto.class)
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Successfully retrieved timers"),
          @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
          @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })
  public List<TimerDto> findAll(final @ApiIgnore Principal principal) {
    final var user = userService.getUser(principal);
    return timerService.findAllByUserId(user.getId())
        .stream()
        .map(e -> timerMapper.toDto(e, DateUtils.SYSTEM_TIMEZONE))
        .collect(Collectors.toList());
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Creates new timer")
  @ApiResponses(value = {
          @ApiResponse(code = 201, message = "Successfully created timer"),
          @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
          @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })
  public ResponseEntity<Void> save(@RequestBody final TimerDto dto,
                                   @ApiIgnore final Principal principal) {
    final var user = userService.getUser(principal);
    timerService.save(timerMapper.toEntity(dto, DateUtils.SYSTEM_TIMEZONE, user));
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PutMapping(path = "/{timerId}/stop")
  @ApiOperation(value = "Stops timer with given id")
  @ApiResponses(value = {
          @ApiResponse(code = 202, message = "Successfully stopped timer"),
          @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
          @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })
  public ResponseEntity<Void> stop(@PathVariable final Long timerId,
                                   @ApiIgnore final Principal principal) {
    final var user = userService.getUser(principal);
    timerService.stopTimer(timerId, user.getId());
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @DeleteMapping("/{timerId}")
  @ApiOperation(value = "Deletes timer id")
  @ApiResponses(value = {
          @ApiResponse(code = 204, message = "Successfully deleted timer"),
          @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
          @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })
  public ResponseEntity<Void> delete(@PathVariable final Long timerId,
                                     @ApiIgnore final Principal principal) {
    final var user = userService.getUser(principal);
    timerService.deleteByIdAndUserId(timerId, user.getId());
    return ResponseEntity.noContent().build();
  }
}
