package com.zor07.nofapp.service;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.entity.*;
import com.zor07.nofapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final RelapseLogRepository relapseLogRepository;
    private final S3Service s3;

    public ProfileService(FileRepository fileRepository,
                          ProfileRepository profileRepository,
                          NoteRepository noteRepository,
                          UserPostsRepository userPostsRepository,
                          RelapseLogRepository relapseLogRepository,
                          S3Service s3) {
        this.fileRepository = fileRepository;
        this.profileRepository = profileRepository;
        this.noteRepository = noteRepository;
        this.userPostsRepository = userPostsRepository;
        this.relapseLogRepository = relapseLogRepository;
        this.s3 = s3;
    }

    public List<Profile> getProfiles() {
        return profileRepository.findAll();
    }

    public Profile getProfile(final Long userId) {
        return profileRepository.getProfileByUserId(userId);
    }

    @Transactional
    public void saveUserAvatar(final Long userId, final MultipartFile multipartFile) throws IOException {
        final var profile = profileRepository.getProfileByUserId(userId);
        var avatar = profile.getAvatar();
        if (avatar == null) {
            avatar = new File();
            avatar.setBucket(USER_BUCKET);
            avatar.setMime(multipartFile.getContentType());
            avatar.setPrefix(String.valueOf(userId));
            avatar.setKey(AVATAR_KEY);
            avatar.setSize(multipartFile.getSize());
        } else {
            avatar.setMime(multipartFile.getContentType());
            avatar.setSize(multipartFile.getSize());
        }
        fileRepository.save(avatar);
        persistAvatarToS3(userId, multipartFile.getBytes());
    }

    @Transactional
    public void relapsed(final Long userId) {
        final var profile = profileRepository.getProfileByUserId(userId);
        saveRelapseLog(profile.getTimerStart());
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

    private void persistAvatarToS3(final Long userId, final byte[] file) {
        final var key = String.format("%s/%s", userId, AVATAR_KEY);
        s3.persistObject(USER_BUCKET, key, file);
    }

    private void saveRelapseLog(final Instant start) {
        final var relapseLog = new RelapseLog();
        relapseLog.setStart(start);
        relapseLog.setStop(Instant.now());
        relapseLogRepository.save(relapseLog);
    }

}
