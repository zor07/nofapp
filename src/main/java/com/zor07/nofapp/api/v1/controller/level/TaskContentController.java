package com.zor07.nofapp.api.v1.controller.level;

import com.zor07.nofapp.api.v1.dto.level.mapper.TaskContentMapper;
import com.zor07.nofapp.service.levels.TaskContentService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/levels/{levelId}/tasks/{taskId}/content")
@Api(tags = "TaskContent")
public class TaskContentController {

    private final TaskContentService taskContentService;
    private final TaskContentMapper taskContentMapper;

    public TaskContentController(final TaskContentService taskContentService,
                                 final TaskContentMapper taskContentMapper) {
        this.taskContentService = taskContentService;
        this.taskContentMapper = taskContentMapper;
    }


//    POST   /api/v1/levels/{levelId}/tasks/{taskId}/content delete task content
//    DELETE /api/v1/levels/{levelId}/tasks/{taskId}/content {id: 1, title: title} create content
//    POST   /api/v1/levels/{levelId}/tasks/{taskId}/content/video - upload video to task
//    POST   /api/v1/levels/{levelId}/tasks/{taskId}/content/text - upload text to task

//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(value = "Gets tasks of level", response = NoteDto.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successfully retrieved tasks"),
//            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
//            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
//    })
//    public ResponseEntity<List<TaskDto>> getTasks(final @PathVariable Long levelId) {
//        return ResponseEntity.ok(
//                taskService.getAllByLevelId(levelId)
//                        .stream()
//                        .map(taskMapper::toDto)
//                        .toList()
//        );
//    }
//
//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(value = "Creates new task", response = TaskDto.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 201, message = "Successfully created new task"),
//            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
//            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
//    })
//    public ResponseEntity<TaskDto> createTask(final @PathVariable Long levelId,
//                                              final @RequestBody TaskDto taskDto) {
//        if (!Objects.equals(taskDto.level().id(), levelId)) {
//            return ResponseEntity.badRequest().build();
//        }
//        final var task = taskService.save(taskMapper.toEntity(taskDto));
//        final var uri = URI.create(ServletUriComponentsBuilder
//                .fromCurrentContextPath()
//                .path(String.format("/api/v1/levels/%d/tasks/%d", levelId, task.getId()))
//                .toUriString());
//        return ResponseEntity.created(uri).body(taskMapper.toDto(task));
//    }
//
//
//    @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(value = "Gets tasks of level", response = NoteDto.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successfully retrieved tasks"),
//            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
//            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
//    })
//    public ResponseEntity<TaskDto> getTask(final @PathVariable Long levelId,
//                                           final @PathVariable Long taskId) {
//        return ResponseEntity.ok(
//            taskMapper.toDto(
//                taskService.getTask(levelId, taskId)
//            )
//        );
//    }
//
//    @PostMapping(
//            value = "/{taskId}",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    @ApiOperation(value = "Updates given task", response = TaskDto.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 201, message = "Successfully updated task"),
//            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
//            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
//    })
//    public ResponseEntity<TaskDto> updateTask(final @PathVariable Long levelId,
//                                              final @PathVariable Long taskId,
//                                              final @RequestBody TaskDto taskDto) {
//        if (!Objects.equals(taskDto.level().id(), levelId) &&
//            !Objects.equals(taskDto.id(), taskId)) {
//            return ResponseEntity.badRequest().build();
//        }
//        final var task = taskService.save(taskMapper.toEntity(taskDto));
//        return ResponseEntity.accepted().body(taskMapper.toDto(task));
//    }
//
//    @DeleteMapping("/{taskId}")
//    @ApiOperation(value = "Delete task by id")
//    @ApiResponses(value = {
//            @ApiResponse(code = 204, message = "Successfully deleted task"),
//            @ApiResponse(code = 401, message = "You are not authorized to update the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
//            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
//    })
//    public ResponseEntity<Void> deleteTask(final @PathVariable Long levelId,
//                                           final @PathVariable Long taskId) {
//        taskService.delete(levelId, taskId);
//        return ResponseEntity.noContent().build();
//    }

}
