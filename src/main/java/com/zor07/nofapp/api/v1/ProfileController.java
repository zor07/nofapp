package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.ProfileDto;
import com.zor07.nofapp.api.v1.mapper.ProfileMapper;
import com.zor07.nofapp.entity.Profile;
import com.zor07.nofapp.service.ProfileService;
import com.zor07.nofapp.service.UserService;
import com.zor07.nofapp.utils.DateUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/profiles")
@Api( tags = "Profile" )
public class ProfileController {

    private final UserService userService;
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;

    public ProfileController(final ProfileService profileService,
                             final ProfileMapper profileMapper,
                             final UserService userService) {
        this.profileService = profileService;
        this.profileMapper = profileMapper;
        this.userService = userService;
    }

    @GetMapping
    @ApiOperation(value = "Retrieves profiles of users", response = ProfileDto.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved profiles"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<List<ProfileDto>> getProfiles() {
        final var profiles = profileService.getProfiles().stream()
                .map(this::mapProfile)
                .toList();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{userId}")
    @ApiOperation(value = "Retrieves profile of given user", response = ProfileDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved profile"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<ProfileDto> getProfile(final @PathVariable Long userId) {
        final var profile = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(mapProfile(profile));
    }

    @PostMapping("/{userId}/avatar")
    @ApiOperation(value = "Uploads avatar image to user profile")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully uploaded avatar"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> uploadAvatar(final @ApiIgnore Principal principal,
                                             final @PathVariable Long userId,
                                             final @RequestParam("file") MultipartFile file) throws IOException {
        final var user = userService.getUser(principal);
        if (!Objects.equals(userId, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        final var data = file.getBytes();
        final var contentType = file.getContentType();
        final var size = file.getSize();
        profileService.saveUserAvatar(userId, data, contentType, size);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{userId}/avatar")
    @ApiOperation(value = "Deletes user avatar")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted avatar"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> deleteAvatar(final @ApiIgnore Principal principal,
                                             final @PathVariable Long userId) {
        final var user = userService.getUser(principal);
        if (!Objects.equals(userId, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        profileService.deleteUserAvatar(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/relapsed")
    @ApiOperation(value = "Restarts user timer and creates relapse log record", response = ProfileDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully restarted user timer and created relapse log record"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<ProfileDto> relapsed(final @ApiIgnore Principal principal,
                                               final @PathVariable Long userId) {
        final var user = userService.getUser(principal);
        if (!Objects.equals(userId, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        profileService.relapsed(user);
        return ResponseEntity.accepted().build();
    }

    private ProfileDto mapProfile(final Profile profile) {
        return profileMapper.toDto(profile, DateUtils.SYSTEM_TIMEZONE);
    }
}
