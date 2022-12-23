package com.zor07.nofapp.service;

import com.zor07.nofapp.aws.s3.S3Service;
import com.zor07.nofapp.entity.file.File;
import com.zor07.nofapp.entity.profile.Profile;
import com.zor07.nofapp.entity.profile.RelapseLog;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.repository.FileRepository;
import com.zor07.nofapp.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;

@Service
public class ProfileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileService.class);

    private static final String USER_BUCKET = "user";
    private static final String AVATAR_KEY = "avatar";
    private final FileRepository fileRepository;
    private final ProfileRepository profileRepository;
    private final RelapseLogService relapseLogService;
    private final S3Service s3;

    public ProfileService(FileRepository fileRepository,
                          ProfileRepository profileRepository,
                          RelapseLogService relapseLogService,
                          S3Service s3) {
        this.fileRepository = fileRepository;
        this.profileRepository = profileRepository;
        this.relapseLogService = relapseLogService;
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

    @Transactional
    public void relapsed(final User user) {
        final var profile = profileRepository.getProfileByUserId(user.getId());
        saveRelapseLog(profile.getTimerStart(), user);
        profile.setTimerStart(Instant.now());
        profileRepository.save(profile);
    }

    private String getAvatarKey(final Long userId, final byte[] data) {
        final var hash = getMD5(data);
        return String.format("%s/%s_%s", userId, AVATAR_KEY, hash);
    }

    public static String getMD5(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input);
            BigInteger number = new BigInteger(1, messageDigest);

            return number.toString(16);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn("Failed to calculate MD5 sum: {0}", e);
            throw new RuntimeException(e);
        }
    }

    private void saveRelapseLog(final Instant start, final User user) {
        final var relapseLog = new RelapseLog();
        relapseLog.setUser(user);
        relapseLog.setStart(start);
        relapseLog.setStop(Instant.now());
        relapseLogService.save(relapseLog);
    }

}
