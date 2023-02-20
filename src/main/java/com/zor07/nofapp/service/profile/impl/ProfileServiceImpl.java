package com.zor07.nofapp.service.profile.impl;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.entity.file.File;
import com.zor07.nofapp.entity.profile.Profile;
import com.zor07.nofapp.entity.profile.RelapseLog;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.repository.file.FileRepository;
import com.zor07.nofapp.repository.profile.ProfileRepository;
import com.zor07.nofapp.service.profile.ProfileService;
import com.zor07.nofapp.service.profile.RelapseLogService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    private static final String USER_BUCKET = "user";
    private static final String AVATAR_KEY = "avatar";
    private final FileRepository fileRepository;
    private final ProfileRepository profileRepository;
    private final RelapseLogService relapseLogService;
    private final S3Service s3;

    public ProfileServiceImpl(FileRepository fileRepository,
                              ProfileRepository profileRepository,
                              RelapseLogService relapseLogService,
                              S3Service s3) {
        this.fileRepository = fileRepository;
        this.profileRepository = profileRepository;
        this.relapseLogService = relapseLogService;
        this.s3 = s3;
    }

    @Override
    public List<Profile> getProfiles() {
        return profileRepository.findAll();
    }

    @Override
    public Profile getProfileByUserId(final Long userId) {
        return profileRepository.getProfileByUserId(userId);
    }

    @Override
    @Transactional
    public void saveUserAvatar(final Long userId,
                               final byte[] data,
                               final String contentType,
                               final long size) {
        final var profile = profileRepository.getProfileByUserId(userId);
        final var key = getAvatarKey(userId, data);
        var avatar = profile.getAvatar();
        if (avatar == null) {
            avatar = new File();
            avatar.setBucket(USER_BUCKET);
            avatar.setMime(contentType);
            avatar.setPrefix(String.valueOf(userId));
            avatar.setKey(key);
            avatar.setSize(size);
        } else {
            s3.deleteObject(USER_BUCKET, avatar.getKey());
            avatar.setKey(key);
            avatar.setMime(contentType);
            avatar.setSize(size);
        }
        fileRepository.save(avatar);
        s3.persistObject(USER_BUCKET, key, data);
        profile.setAvatar(avatar);
        profileRepository.save(profile);
    }

    @Override
    @Transactional
    public void deleteUserAvatar(final Long userId) {
        final var profile = profileRepository.getProfileByUserId(userId);
        final var avatar = profile.getAvatar();
        if (avatar == null) {
            return;
        }
        final var key = avatar.getKey();
        profile.setAvatar(null);
        profileRepository.save(profile);
        fileRepository.delete(avatar);
        s3.deleteObject(USER_BUCKET, key);
    }

    @Override
    @Transactional
    public void relapsed(final User user) {
        final var profile = profileRepository.getProfileByUserId(user.getId());
        saveRelapseLog(profile.getTimerStart(), user);
        profile.setTimerStart(Instant.now());
        profileRepository.save(profile);
    }

    private String getAvatarKey(final Long userId, final byte[] data) {
        final var hash = s3.getMD5(data);
        return String.format("%s/%s_%s", userId, AVATAR_KEY, hash);
    }

    private void saveRelapseLog(final Instant start, final User user) {
        final var relapseLog = new RelapseLog();
        relapseLog.setUser(user);
        relapseLog.setStart(start);
        relapseLog.setStop(Instant.now());
        relapseLogService.save(relapseLog);
    }

}
