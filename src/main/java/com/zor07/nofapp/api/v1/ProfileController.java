package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.ProfileDto;
import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.service.ProfileService;
import com.zor07.nofapp.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
@Api( tags = "Profile" )
public class ProfileController {

    private final S3Service s3Service;
    private final UserService userService;

    private final ProfileService profileService;

    public ProfileController(final S3Service s3Service,
                             final UserService userService,
                             final ProfileService profileService) {
        this.s3Service = s3Service;
        this.userService = userService;
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<List<ProfileDto>> getProfiles(final Principal principal) {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDto> getProfile(final Principal principal,
                                                 final @PathVariable Long userId) {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<Void> uploadAvatar(final Principal principal,
                                             final @PathVariable Long userId,
                                             final @RequestParam("file") MultipartFile file){
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<Void> deleteAvatar(final Principal principal) {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/{userId}/relapsed")
    public ResponseEntity<ProfileDto> relapsed(final Principal principal,
                                               final @PathVariable Long userId) {

        throw new UnsupportedOperationException();
    }

    @PostMapping("/{userId}/posts/{noteId}")
    public ResponseEntity<ProfileDto> addPostToProfile(final Principal principal,
                                                       final @PathVariable Long userId) {
        throw new UnsupportedOperationException();
    }

}
