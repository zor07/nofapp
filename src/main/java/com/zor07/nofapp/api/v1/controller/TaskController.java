package com.zor07.nofapp.api.v1.controller;

import com.zor07.nofapp.api.v1.dto.level.TaskDto;
import com.zor07.nofapp.api.v1.dto.level.mapper.TaskMapper;
import com.zor07.nofapp.api.v1.dto.notes.NoteDto;
import com.zor07.nofapp.service.levels.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/levels/{levelId}/tasks")
@Api(tags = "Task")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(final TaskService taskService,
                          final TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Gets tasks of level", response = NoteDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved tasks"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<List<TaskDto>> getTasks(final @PathVariable Long levelId) {
        return ResponseEntity.ok(
                taskService.getAllByLevelId(levelId)
                        .stream()
                        .map(taskMapper::toDto)
                        .toList()
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Creates new level", response = TaskDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created new level"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<TaskDto> createTask(final @PathVariable Long levelId,
                                              final @RequestBody TaskDto taskDto) {
        if (!Objects.equals(taskDto.level().id(), levelId)) {
            return ResponseEntity.badRequest().build();
        }
        final var task = taskService.save(taskMapper.toEntity(taskDto));
        final var uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(String.format("/api/v1/levels/%d/tasks/%d", levelId, task.getId()))
                .toUriString());
        return ResponseEntity.created(uri).body(taskMapper.toDto(task));
    }


    @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Gets tasks of level", response = NoteDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved tasks"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<TaskDto> getTask(final @PathVariable Long levelId,
                                           final @PathVariable Long taskId) {
        return ResponseEntity.ok(
            taskMapper.toDto(
                taskService.getTask(levelId, taskId)
            )
        );
    }

    @PostMapping(
            value = "/{taskId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(value = "Updates given task", response = TaskDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully updated task"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<TaskDto> updateTask(final @PathVariable Long levelId,
                                              final @PathVariable Long taskId,
                                              final @RequestBody TaskDto taskDto) {
        if (!Objects.equals(taskDto.level().id(), levelId) &&
            !Objects.equals(taskDto.id(), taskId)) {
            return ResponseEntity.badRequest().build();
        }
        final var task = taskService.save(taskMapper.toEntity(taskDto));
        return ResponseEntity.accepted().body(taskMapper.toDto(task));

    }




//    DELETE /api/vi/levels/{levelId}/tasks/{taskId} - delete task
//
//    TaskService
//    getAllByLevelId(Long levelId)
//    save(Task task)
//    delete(Long id)
//    deleteTaskContent(Long taskId)
//    createTaskContent(TaskContent content)
//    addVideoToTask(taskId, file)
//    addTextToTask(taskId, jsonNode)
}
