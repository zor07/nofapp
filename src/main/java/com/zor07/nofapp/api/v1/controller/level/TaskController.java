package com.zor07.nofapp.api.v1.controller.level;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zor07.nofapp.api.v1.dto.level.TaskDto;
import com.zor07.nofapp.api.v1.dto.level.mapper.TaskMapper;
import com.zor07.nofapp.service.levels.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
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
    @ApiOperation(value = "Gets tasks of level", response = TaskDto.class)
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
                        .map(task -> {
                            try {
                                return taskMapper.toDto(task);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList()
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Creates new task", response = TaskDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created new task"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<TaskDto> createTask(final @PathVariable Long levelId,
                                              final @RequestBody TaskDto taskDto) throws JsonProcessingException {
        final var task = taskService.save(levelId, taskMapper.toEntity(taskDto));
        final var uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(String.format("/api/v1/levels/%d/tasks/%d", levelId, task.getId()))
                .toUriString());
        return ResponseEntity.created(uri).body(taskMapper.toDto(task));
    }


    @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Gets tasks of level", response = TaskDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved task"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<TaskDto> getTask(final @PathVariable Long levelId,
                                           final @PathVariable Long taskId) throws JsonProcessingException {
        return ResponseEntity.ok(
            taskMapper.toDto(
                taskService.getTask(levelId, taskId)
            )
        );
    }

    @GetMapping(value = "/{taskId}/next", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Gets next task after task with given taskId", response = TaskDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved task"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<TaskDto> getNextTask(final @PathVariable Long levelId,
                                               final @PathVariable Long taskId) throws JsonProcessingException {
        final var task = taskService.getTask(levelId, taskId);
        return ResponseEntity.ok(
                taskMapper.toDto(
                        taskService.findNextTask(task)
                )
        );
    }

    @GetMapping(value = "/{taskId}/prev", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Gets previous task before task with given taskId", response = TaskDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved task"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<TaskDto> getPrevTask(final @PathVariable Long levelId,
                                               final @PathVariable Long taskId) throws JsonProcessingException {
        final var task = taskService.getTask(levelId, taskId);
        return ResponseEntity.ok(
                taskMapper.toDto(
                        taskService.findPrevTask(task)
                )
        );
    }

    @PutMapping(
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
                                              final @RequestBody TaskDto taskDto) throws JsonProcessingException {
        if (!Objects.equals(taskDto.id(), taskId)) {
            return ResponseEntity.badRequest().build();
        }
        final var task = taskService.save(levelId, taskMapper.toEntity(taskDto));
        return ResponseEntity.accepted().body(taskMapper.toDto(task));
    }

    @DeleteMapping("/{taskId}")
    @ApiOperation(value = "Delete task by id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted task"),
            @ApiResponse(code = 401, message = "You are not authorized to update the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> deleteTask(final @PathVariable Long levelId,
                                           final @PathVariable Long taskId) {
        taskService.delete(levelId, taskId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{taskId}/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "Uploads video to task")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully uploaded video"),
            @ApiResponse(code = 401, message = "You are not authorized to update the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> uploadVideo(final @PathVariable Long levelId,
                                            final @PathVariable Long taskId,
                                            final @RequestParam("file") MultipartFile file) throws IOException {
        taskService.addVideo(levelId, taskId, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{taskId}/video")
    @ApiOperation(value = "Deletes video from task")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted video"),
            @ApiResponse(code = 401, message = "You are not authorized to update the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> deleteVideo(final @PathVariable Long levelId,
                                            final @PathVariable Long taskId) {
        taskService.deleteVideo(levelId, taskId);
        return ResponseEntity.noContent().build();
    }

}
