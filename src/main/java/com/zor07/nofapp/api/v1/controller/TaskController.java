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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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



//    GET    /api/v1/levels/{levelId}/tasks/{taskId} TaskDto
//    POST   /api/vi/levels/{levelId}/tasks TaskDto - create task
//    PUT    /api/vi/levels/{levelId}/tasks/{taskId} TaskDto - update task
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
