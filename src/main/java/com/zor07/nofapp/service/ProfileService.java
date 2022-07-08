package com.zor07.nofapp.service;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.entity.File;
import com.zor07.nofapp.entity.Profile;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.entity.UserPost;
import com.zor07.nofapp.repository.FileRepository;
import com.zor07.nofapp.repository.NoteRepository;
import com.zor07.nofapp.repository.ProfileRepository;
import com.zor07.nofapp.repository.UserPostsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
public class ProfileService {

    private static final String USER_BUCKET = "user";
    private static final String AVATAR_KEY = "avatar";

    private final FileRepository fileRepository;
    private final ProfileRepository profileRepository;
    private final NoteRepository noteRepository;
    private final UserPostsRepository userPostsRepository;
    private final S3Service s3;

    public ProfileService(FileRepository fileRepository,
                          ProfileRepository profileRepository,
                          NoteRepository noteRepository,
                          UserPostsRepository userPostsRepository,
                          S3Service s3) {
        this.fileRepository = fileRepository;
        this.profileRepository = profileRepository;
        this.noteRepository = noteRepository;
        this.userPostsRepository = userPostsRepository;
        this.s3 = s3;
    }

    @PostConstruct
    private void createBucket() {
        if (!s3.containsBucket(USER_BUCKET)) {
            s3.createBucket(USER_BUCKET);
        }
    }

    public List<Profile> getProfiles() {
        return profileRepository.findAll();
    }

    public Profile getProfile(final Long userId) {
        return profileRepository.getProfileByUserId(userId);
    }

    @Transactional
    public void saveUserAvatar(final Long userId, final MultipartFile multipartFile) throws IOException {
        persistFile(userId, multipartFile);
        persistFileToS3(userId, multipartFile.getBytes());
    }

    public void relapsed(final Long userId) {
        final var profile = profileRepository.getProfileByUserId(userId);
        profile.setTimerStart(Instant.now());
        profileRepository.save(profile);
    }

    public void addPostToProfile(final User user, final Long noteId) {
        final var post = new UserPost();
        post.setUser(user);
        post.setNote(noteRepository.getById(noteId));
        userPostsRepository.save(post);
    }

    public void removePostFromProfile(final Long userId, final Long noteId){
        userPostsRepository.deleteUserPostByUserIdAndNoteId(userId, noteId);
    }

    private void persistFile(final Long userId, final MultipartFile multipartFile) {
        final var file = new File();
        file.setBucket(USER_BUCKET);
        file.setMime(multipartFile.getContentType());
        file.setPrefix(String.valueOf(userId));
        file.setKey(AVATAR_KEY);
        file.setSize(multipartFile.getSize());
        fileRepository.save(file);
    }

    private void persistFileToS3(final Long userId, final byte[] file) {
        final var key = String.format("%s/%s", userId, AVATAR_KEY);
        s3.persistObject(USER_BUCKET, key, file);
    }

}
