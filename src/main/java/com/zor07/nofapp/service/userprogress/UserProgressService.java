package com.zor07.nofapp.service.userprogress;

import com.zor07.nofapp.entity.level.TaskContent;
import com.zor07.nofapp.entity.userprogress.UserProgress;
import com.zor07.nofapp.entity.user.User;

import java.util.List;

public interface UserProgressService {
    void initUserProgress(User user);

    UserProgress updateUserProgressToNextTask(User user);

    List<TaskContent> getCurrentTaskContentForUser(User user);
}
