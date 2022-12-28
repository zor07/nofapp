package com.zor07.nofapp.api.v1.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/levels/{levelId}/tasks")
@Api(tags = "Task")
public class TaskController {

}
