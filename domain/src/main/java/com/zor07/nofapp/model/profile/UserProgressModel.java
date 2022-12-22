package com.zor07.nofapp.model.profile;

import com.zor07.nofapp.model.levels.TaskModel;
import com.zor07.nofapp.model.user.UserModel;

public record UserProgressModel(
        Long id,
        UserModel user,
        TaskModel currentTask
) {


}
