package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.ProfileDto;
import com.zor07.nofapp.api.v1.mapper.ProfileMapper;
import com.zor07.nofapp.entity.Profile;
import com.zor07.nofapp.service.ProfileService;
import com.zor07.nofapp.service.UserService;
import com.zor07.nofapp.utils.DateUtils;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<List<ProfileDto>> getProfiles() {
        final var profiles = profileService.getProfiles().stream()
                .map(this::mapProfile)
                .toList();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDto> getProfile(final @PathVariable Long userId) {
        final var profile = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(mapProfile(profile));
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<Void> uploadAvatar(final Principal principal,
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
    public ResponseEntity<Void> deleteAvatar(final Principal principal,
                                             final @PathVariable Long userId) {
        final var user = userService.getUser(principal);
        if (!Objects.equals(userId, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        profileService.deleteUserAvatar(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/relapsed")
    public ResponseEntity<ProfileDto> relapsed(final Principal principal,
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
