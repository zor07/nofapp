package com.zor07.nofapp.domain.model.profile;

import com.zor07.nofapp.domain.model.levels.TaskModel;
import com.zor07.nofapp.domain.model.user.UserModel;

public record UserProgressModel(
        Long id,
        UserModel user,
        TaskModel currentTask
) {


}
