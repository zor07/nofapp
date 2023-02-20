package com.zor07.nofapp.service.profile;

import com.zor07.nofapp.entity.profile.Profile;
import com.zor07.nofapp.entity.user.User;

import javax.transaction.Transactional;
import java.util.List;

public interface ProfileService {
    List<Profile> getProfiles();

    Profile getProfileByUserId(Long userId);

    @Transactional
    void saveUserAvatar(Long userId,
                        byte[] data,
                        String contentType,
                        long size);

    @Transactional
    void deleteUserAvatar(Long userId);

    @Transactional
    void relapsed(User user);
}
