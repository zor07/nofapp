package com.zor07.nofapp.test;

import com.zor07.nofapp.entity.level.Task;
import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.entity.userprogress.UserProgress;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProgresTestUtils {

    public static UserProgress getBlankEntity(final User user, final Task task) {
        final var userProgress = new UserProgress();
        userProgress.setUser(user);
        userProgress.setTask(task);
        return userProgress;
    }

    public static void checkUpdated(final UserProgress userProgress) {
        TaskTestUtils.checkUpdated(userProgress.getTask());
    }

    public static void checkEntity(
            final UserProgress actual,
            final UserProgress expected,
            final boolean checkId
    ) {
        if (checkId) {
            assertThat(actual.getId()).isEqualTo(expected.getId());
        }
        UserTestUtils.checkEntity(actual.getUser(), expected.getUser(), checkId);
        TaskTestUtils.checkEntity(actual.getTask(), expected.getTask(), checkId);
    }

}
