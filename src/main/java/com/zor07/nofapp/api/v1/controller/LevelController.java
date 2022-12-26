package com.zor07.nofapp.api.v1.controller;

import com.zor07.nofapp.service.levels.LevelService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/levels")
@Api(tags = "Level")
public class LevelController {


    private final LevelService levelService;

    public LevelController(LevelService levelService) {
        this.levelService = levelService;
    }




}
