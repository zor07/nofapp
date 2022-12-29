package com.zor07.nofapp.api.v1.controller;

import com.zor07.nofapp.api.v1.dto.level.mapper.TaskMapper;
import com.zor07.nofapp.service.levels.TaskService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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





//    GET    /api/v1/levels/{levelId}/tasks List<TaskDto>
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
