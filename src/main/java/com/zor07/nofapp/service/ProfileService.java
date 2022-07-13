package com.zor07.nofapp.service;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.entity.File;
import com.zor07.nofapp.entity.Profile;
import com.zor07.nofapp.entity.RelapseLog;
import com.zor07.nofapp.entity.User;
import com.zor07.nofapp.entity.UserPost;
import com.zor07.nofapp.repository.FileRepository;
import com.zor07.nofapp.repository.NoteRepository;
import com.zor07.nofapp.repository.ProfileRepository;
import com.zor07.nofapp.repository.RelapseLogRepository;
import com.zor07.nofapp.repository.UserPostsRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    public Profile getProfileByUserId(final Long userId) {
        return profileRepository.getProfileByUserId(userId);
    }

    @Transactional
    public void saveUserAvatar(final Long userId,
                               final byte[] data,
                               final String contentType,
                               final long size) {
        final var profile = profileRepository.getProfileByUserId(userId);
        var avatar = profile.getAvatar();
        if (avatar == null) {
            avatar = new File();
            avatar.setBucket(USER_BUCKET);
            avatar.setMime(contentType);
            avatar.setPrefix(String.valueOf(userId));
            avatar.setKey(AVATAR_KEY);
            avatar.setSize(size);
        } else {
            avatar.setMime(contentType);
            avatar.setSize(size);
        }
        fileRepository.save(avatar);
        persistAvatarToS3(userId, data);
    }

    @Transactional
    public void deleteUserAvatar(final Long userId) {
        final var profile = profileRepository.getProfileByUserId(userId);
        var avatar = profile.getAvatar();
        if (avatar == null) {
            return;
        }
        profile.setAvatar(null);
        profileRepository.save(profile);
        fileRepository.delete(avatar);
        s3.deleteObject(USER_BUCKET, String.format("%s/%s", userId, AVATAR_KEY));
    }

    @Transactional
    public void relapsed(final User user) {
        final var profile = profileRepository.getProfileByUserId(user.getId());
        saveRelapseLog(profile.getTimerStart(), user);
        profile.setTimerStart(Instant.now());
        profileRepository.save(profile);
    }

    public void addPostToProfile(final User user, final Long noteId) {
        final var post = new UserPost();
        post.setUser(user);
        post.setNote(noteRepository.getById(noteId));
        userPostsRepository.save(post);
    }

    @Transactional
    public void removePostFromProfile(final Long userId, final Long noteId){
        userPostsRepository.deleteUserPostByUserIdAndNoteId(userId, noteId);
    }

    private void persistAvatarToS3(final Long userId, final byte[] data) {
        final var key = String.format("%s/%s", userId, AVATAR_KEY);
        s3.persistObject(USER_BUCKET, key, data);
    }

    private void saveRelapseLog(final Instant start, final User user) {
        final var relapseLog = new RelapseLog();
        relapseLog.setUser(user);
        relapseLog.setStart(start);
        relapseLog.setStop(Instant.now());
        relapseLogRepository.save(relapseLog);
    }

}
