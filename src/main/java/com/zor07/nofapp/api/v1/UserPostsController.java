package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.ProfileDto;
import com.zor07.nofapp.service.UserPostService;
import com.zor07.nofapp.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/profiles/{userId}/posts")
@Api( tags = "User posts" )
public class UserPostsController {

    private final UserService userService;

    private final UserPostService userPostService;

    public UserPostsController(final UserService userService,
                               final UserPostService userPostService) {
        this.userService = userService;
        this.userPostService = userPostService;
    }

    @PostMapping("/{noteId}")
    public ResponseEntity<ProfileDto> addPostToProfile(final Principal principal,
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
    public ResponseEntity<ProfileDto> removePostFromProfile(final Principal principal,
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
