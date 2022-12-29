package com.zor07.nofapp.api.v1.controller.level;

import com.zor07.nofapp.api.v1.dto.level.TaskContentDto;
import com.zor07.nofapp.api.v1.dto.level.mapper.TaskContentMapper;
import com.zor07.nofapp.service.levels.TaskContentService;
import com.zor07.nofapp.service.levels.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/levels/{levelId}/tasks/{taskId}/content")
@Api(tags = "TaskContent")
public class TaskContentController {

    private final TaskContentService taskContentService;
    private final TaskService taskService;
    private final TaskContentMapper taskContentMapper;

    public TaskContentController(final TaskContentService taskContentService,
                                 final TaskService taskService,
                                 final TaskContentMapper taskContentMapper) {
        this.taskContentService = taskContentService;
        this.taskService = taskService;
        this.taskContentMapper = taskContentMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Creates new task content", response = TaskContentDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created new task content"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<TaskContentDto> createTaskContent(final @PathVariable Long levelId,
                                                            final @PathVariable Long taskId,
                                                            final @RequestBody TaskContentDto taskContentDto) {

        taskContentService.save(taskContentMapper.toEntity(taskContentDto));
        final var uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(String.format("/api/v1/levels/%d/tasks/%d", levelId, taskId))
                .toUriString());
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping
    @ApiOperation(value = "Delete task by id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted task"),
            @ApiResponse(code = 401, message = "You are not authorized to update the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> deleteTaskContent(final @PathVariable Long levelId,
                                                  final @PathVariable Long taskId) {
        taskContentService.deleteByLevelIdAndTaskId(levelId, taskId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/video")
    @ApiOperation(value = "Delete task by id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted task"),
            @ApiResponse(code = 401, message = "You are not authorized to update the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> uploadVideo(final @PathVariable Long levelId,
                                            final @PathVariable Long taskId,
                                            @RequestParam("file") MultipartFile file) throws IOException {
        taskContentService.addVideo(levelId, taskId, file);
        return ResponseEntity.accepted().build();
    }

    @PutMapping(
            value = "/{taskContentId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(value = "Updates given task", response = TaskContentDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully updated task"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<TaskContentDto> updateTaskContent(final @PathVariable Long levelId,
                                                            final @PathVariable Long taskId,
                                                            final @PathVariable Long taskContentId,
                                                            final @RequestBody TaskContentDto taskDto) {
        final var task = taskService.getTask(levelId, taskId);
        if (!Objects.equals(taskContentId, taskDto.id()) &&
            !Objects.equals(task.getId(), taskContentId)) {
            return ResponseEntity.badRequest().build();
        }
        taskContentService.save(taskContentMapper.toEntity(taskDto));
        return ResponseEntity.accepted().build();
    }

}
