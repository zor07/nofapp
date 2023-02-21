package com.zor07.nofapp.service.profile;

import com.zor07.nofapp.entity.level.TaskContent;
import com.zor07.nofapp.entity.profile.UserProgress;
import com.zor07.nofapp.entity.user.User;

import java.util.List;

public interface UserProgressService {
    void initUserProgress(User user);

    UserProgress updateUserProgressToNextTask(User user);

    List<TaskContent> getCurrentTaskContentForUser(User user);
}
