package com.zor07.nofapp.domain.model.profile;

import com.zor07.nofapp.domain.model.levels.Task;
import com.zor07.nofapp.domain.model.user.User;

public record UserProgress(
        Long id,
        User user,
        Task currentTask
) {


}
