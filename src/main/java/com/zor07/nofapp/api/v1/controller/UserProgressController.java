package com.zor07.nofapp.api.v1.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zor07.nofapp.api.v1.dto.level.TaskContentDto;
import com.zor07.nofapp.api.v1.dto.level.mapper.TaskContentMapper;
import com.zor07.nofapp.service.userprogress.UserProgressService;
import com.zor07.nofapp.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/progress")
@Api( tags = "User progress" )
public class UserProgressController {

    private final UserService userService;
    private final UserProgressService userProgressService;
    private final TaskContentMapper taskContentMapper;

    public UserProgressController(final UserService userService,
                                  final UserProgressService userProgressService,
                                  final TaskContentMapper taskContentMapper) {
        this.userService = userService;
        this.userProgressService = userProgressService;
        this.taskContentMapper = taskContentMapper;
    }

    @GetMapping
    @ApiOperation(value = "Retrieves user task content", response = TaskContentDto.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved task content"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<List<TaskContentDto>> getCurrentTaskContentForUser(final @ApiIgnore Principal principal) {
        final var user = userService.getUser(principal);
        final var taskContentList = userProgressService.getCurrentTaskContentForUser(user)
                .stream()
                .map(taskContent -> {
                    try {
                        return taskContentMapper.toDto(taskContent);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        return ResponseEntity.ok(taskContentList);
    }

    @PutMapping(path = "/nextTask")
    @ApiOperation(value = "Updates user progress to next task")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully updated user progress to next task"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> updateUserProgressToNextTask(@ApiIgnore final Principal principal) {
        final var user = userService.getUser(principal);
        userProgressService.updateUserProgressToNextTask(user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
