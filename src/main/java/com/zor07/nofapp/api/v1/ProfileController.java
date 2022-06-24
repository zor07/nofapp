package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/profile/{userId}")
@Api( tags = "Profile" )
public class ProfileController {

    private final S3Service s3Service;
    private final UserService userService;

    public ProfileController(final S3Service s3Service,
                             final UserService userService) {
        this.s3Service = s3Service;
        this.userService = userService;
    }

    @PostMapping("/avatar")
    public ResponseEntity<Void> uploadAvatar(final Principal principal,
                                             final @RequestParam("file") MultipartFile file) throws IOException {
        final var user = userService.getUser(principal);

        final var bucket = "user";


//        s3Service.createBucket("user");
        if (!s3Service.containsBucket(bucket)) {
            s3Service.createBucket(bucket);
        }

        s3Service.persistObject(bucket, String.format("%s/avatar", user.getId()), file.getBytes());
        return ResponseEntity.ok().build();
    }

}
