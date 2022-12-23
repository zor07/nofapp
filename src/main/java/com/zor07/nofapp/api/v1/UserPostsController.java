package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.notes.NoteDto;
import com.zor07.nofapp.api.v1.mapper.NoteMapper;
import com.zor07.nofapp.service.profile.UserPostService;
import com.zor07.nofapp.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/profiles/{userId}/posts")
@Api( tags = "User posts" )
public class UserPostsController {

    private final UserService userService;

    private final UserPostService userPostService;

    private final NoteMapper noteMapper;

    public UserPostsController(final UserService userService,
                               final UserPostService userPostService,
                               final NoteMapper noteMapper) {
        this.userService = userService;
        this.userPostService = userPostService;
        this.noteMapper = noteMapper;
    }

    @GetMapping
    @ApiOperation(value = "Retrieves user posts", response = NoteDto.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved user posts"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<List<NoteDto>> getUserPosts(final @PathVariable Long userId) {
        final var notes = userPostService.getUserPosts(userId)
                .stream()
                .map(noteMapper::toDto)
                .toList();

        return ResponseEntity.ok(notes);
    }


    @PostMapping("/{noteId}")
    @ApiOperation(value = "Adds user post")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully added user post"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> addPostToProfile(final @ApiIgnore Principal principal,
                                                 final @PathVariable Long userId,
                                                 final @PathVariable Long noteId) {
        final var user = userService.getUser(principal);
        if (!Objects.equals(userId, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userPostService.addPostToUser(user, noteId);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{noteId}")
    @ApiOperation(value = "Removes user post")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully removed user post"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> removePostFromProfile(final @ApiIgnore Principal principal,
                                                      final @PathVariable Long userId,
                                                      final @PathVariable Long noteId) {
        final var user = userService.getUser(principal);
        if (!Objects.equals(userId, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userPostService.removePostFromUser(userId, noteId);
        return ResponseEntity.noContent().build();
    }

}
