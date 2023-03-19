package com.zor07.nofapp.service.userprogress;

import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.entity.userprogress.UserProgress;

import java.util.List;

public interface UserProgressService {
    void initUserProgress(User user);

    UserProgress addNextTaskToUserProgress(User user);

    Task getCurrentTaskForUser(User user);

    List<UserProgress> getUserProgress(User user);
}
