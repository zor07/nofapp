package com.zor07.nofapp.service.profile;

import com.zor07.nofapp.entity.profile.UserProgress;
import com.zor07.nofapp.entity.user.User;

public interface UserProgressService {
    UserProgress getUserProgress(User user);

    void initUserProgress(User user);

    UserProgress setNextTaskInUserProgress(User user);
}
