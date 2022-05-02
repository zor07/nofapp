package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.PracticeDto;
import com.zor07.nofapp.api.v1.mapper.PracticeMapper;
import com.zor07.nofapp.service.PracticeService;
import com.zor07.nofapp.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/practice")
@Api(tags = "Practices")
public class PracticeController {

    private final UserService userService;
    private final PracticeService practiceService;
    private final PracticeMapper practiceMapper;
    @Autowired
    public PracticeController(final UserService userService,
                              final PracticeService practiceService,
                              final PracticeMapper practiceMapper) {
        this.userService = userService;
        this.practiceService = practiceService;
        this.practiceMapper = practiceMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Retrieves list of practices", response = PracticeDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of practices"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public List<PracticeDto> getPractices(@RequestParam(defaultValue = "false") final boolean isPublic,
                                          final @ApiIgnore Principal principal) {
        final var user = userService.getUser(principal);

        final var practices = isPublic
                ? practiceService.getPublicPractices()
                : practiceService.getUserPractices(user.getId());

        return practices.stream()
                .map(practiceMapper::toDto)
                .toList();
    }

    @GetMapping(path = "/{practiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Retrieves practice by id", response = PracticeDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved practice"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<PracticeDto> getPractice(@PathVariable final Long practiceId,
                                                   final @ApiIgnore Principal principal) {
        final var user = userService.getUser(principal);
        final var practice = practiceService.getPracticeForUser(practiceId, user);
        return ResponseEntity.ok(practiceMapper.toDto(practice));
    }

    @PostMapping("/{practiceId}/userPractice")
    @ApiOperation(value = "Adds practice to current user")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully added practice to user"),
            @ApiResponse(code = 401, message = "You are not authorized to access the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> addPracticeToUser(@PathVariable final Long practiceId,
                                                  final @ApiIgnore Principal principal) {
        final var user = userService.getUser(principal);
        practiceService.addPracticeToUser(practiceId, user);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{practiceId}/userPractice")
    @ApiOperation(value = "Removes practice from user")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully removed practice from user"),
            @ApiResponse(code = 401, message = "You are not authorized to access the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> removePracticeFromUser(@PathVariable final Long practiceId,
                                                       final @ApiIgnore Principal principal) {
        final var user = userService.getUser(principal);
        practiceService.removePracticeFromUser(practiceId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Creates practice")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created practice"),
            @ApiResponse(code = 401, message = "You are not authorized to access the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<PracticeDto> savePractice(@RequestBody final PracticeDto practiceDto,
                                                    final @ApiIgnore Principal principal) {
        final var user = userService.getUser(principal);
        final var practice = practiceService.savePractice(practiceMapper.toEntity(practiceDto), user);
        final var uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(String.format("/api/v1/practice/%s", practice.getId()))
                .toUriString());
        return ResponseEntity.created(uri).body(practiceMapper.toDto(practice));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates practice")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully updated practice"),
            @ApiResponse(code = 401, message = "You are not authorized to access the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<PracticeDto> updatePractice(@RequestBody final PracticeDto practiceDto,
                                                      final @ApiIgnore Principal principal) {
        final var user = userService.getUser(principal);
        final var practice = practiceService.updatePractice(practiceMapper.toEntity(practiceDto), user);
        return ResponseEntity.accepted().body(practiceMapper.toDto(practice));
    }

    @DeleteMapping("/{practiceId}")
    @ApiOperation(value = "Deletes practice")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted practice"),
            @ApiResponse(code = 401, message = "You are not authorized to access the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> deletePractice(@PathVariable final Long practiceId,
                                               final @ApiIgnore Principal principal) {
        final var user = userService.getUser(principal);
        practiceService.deletePractice(practiceId, user);
        return ResponseEntity.noContent().build();
    }
}
