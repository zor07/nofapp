package com.zor07.nofapp.api.v1.controller;

import com.zor07.nofapp.api.v1.dto.level.mapper.LevelMapper;
import com.zor07.nofapp.service.levels.LevelService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/levels")
@Api(tags = "Level")
public class LevelController {


    private final LevelService levelService;

    private final LevelMapper levelMapper;

    public LevelController(LevelService levelService, LevelMapper levelMapper) {
        this.levelService = levelService;
        this.levelMapper = levelMapper;
    }


//    GET    /api/v1/levels  List<LevelDto>
//    POST   /api/v1/levels create Level
//    PUT    /api/v1/levels/{levelId} update level
//    DELETE /api/v1/levels/{levelId} delete level

//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(value = "Gets all levels", response = NoteDto.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successfully retrieved levels"),
//            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
//            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
//    })
//    public ResponseEntity<List<LevelDto>> getLevels(final @PathVariable Long levelId) {
//        return ResponseEntity.ok(noteMapper.toDto(noteService.getNote(notebookId, noteId, userId)));
//    }




}
