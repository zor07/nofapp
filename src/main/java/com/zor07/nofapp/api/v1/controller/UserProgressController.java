package com.zor07.nofapp.api.v1.controller;

import com.zor07.nofapp.api.v1.dto.userprogress.UserProgressDto;
import com.zor07.nofapp.api.v1.dto.userprogress.mapper.UserProgressMapper;
import com.zor07.nofapp.service.user.UserService;
import com.zor07.nofapp.service.userprogress.UserProgressService;
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

@RestController
@RequestMapping("/api/v1/progress")
@Api( tags = "User progress" )
public class UserProgressController {

    private final UserService userService;
    private final UserProgressService userProgressService;
    private final UserProgressMapper userProgressMapper;

    public UserProgressController(final UserService userService,
                                  final UserProgressService userProgressService,
                                  final UserProgressMapper userProgressMapper) {
        this.userService = userService;
        this.userProgressService = userProgressService;
        this.userProgressMapper = userProgressMapper;
    }

    @GetMapping
    @ApiOperation(value = "Gets user progress", response = UserProgressDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated user progress"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<UserProgressDto> getUserProgress(@ApiIgnore final Principal principal) {
        final var user = userService.getUser(principal);
        final var userProgressList = userProgressService.getUserProgress(user);
        return ResponseEntity.ok(userProgressMapper.toDto(userProgressList));
    }

    // todo init user progress
    @PutMapping(path = "/finishCurrentTask")
    @ApiOperation(value = "Updates user progress to next task")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully updated user progress to next task"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> updateUserProgressToNextTask(@ApiIgnore final Principal principal) {
        final var user = userService.getUser(principal);
        userProgressService.addNextTaskToUserProgress(user);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
