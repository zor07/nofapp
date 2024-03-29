package com.zor07.nofapp.api.v1.controller.level;

import com.zor07.nofapp.api.v1.dto.level.LevelDto;
import com.zor07.nofapp.api.v1.dto.level.mapper.LevelMapper;
import com.zor07.nofapp.service.levels.LevelService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/levels")
@Api(tags = "Level")
public class LevelController {

    private final LevelService levelService;
    private final LevelMapper levelMapper;

    public LevelController(LevelService levelService,
                           LevelMapper levelMapper) {
        this.levelService = levelService;
        this.levelMapper = levelMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Gets all levels", response = LevelDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved levels"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<List<LevelDto>> getLevels() {
        final var all = levelService.getAll();
//        all.forEach(Level::getTasks);
        return ResponseEntity.ok(

                all
                        .stream()
                        .map(levelMapper::toDto)
                        .toList()
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Creates new level", response = LevelDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created new level"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<LevelDto> createLevel(final @RequestBody LevelDto levelDto) {
        final var level = levelService.save(levelMapper.toEntity(levelDto));
        final var uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(String.format("/api/v1/levels/%d", level.getId()))
                .toUriString());
        return ResponseEntity.created(uri).body(levelMapper.toDto(level));
    }

    @PutMapping(
            value = "/{levelId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(value = "Update level", response = LevelDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully updated level"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<LevelDto> updateLevel(final @RequestBody LevelDto levelDto,
                                                final @PathVariable Long levelId) {
        if (!Objects.equals(levelDto.id(), levelId)) {
            return ResponseEntity.badRequest().build();
        }
        final var level = levelService.save(levelMapper.toEntity(levelDto));
        return ResponseEntity.accepted().body(levelMapper.toDto(level));
    }

    @DeleteMapping("/{levelId}")
    @ApiOperation(value = "Delete level by id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted level"),
            @ApiResponse(code = 401, message = "You are not authorized to update the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> deleteLevel(final @PathVariable Long levelId) {
        levelService.delete(levelId);
        return ResponseEntity.noContent().build();
    }

}
