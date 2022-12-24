package com.zor07.nofapp.test;

import com.zor07.nofapp.entity.user.User;
import com.zor07.nofapp.entity.profile.UserProgress;
import com.zor07.nofapp.entity.level.Task;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProgresTestUtils {

    public static UserProgress getBlankEntity(final User user, final Task task) {
        final var userProgress = new UserProgress();
        userProgress.setUser(user);
        userProgress.setCurrentTask(task);
        return userProgress;
    }

    public static void checkUpdated(final UserProgress userProgress) {
        TaskTestUtils.checkUpdated(userProgress.getCurrentTask());
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
        TaskTestUtils.checkEntity(actual.getCurrentTask(), expected.getCurrentTask(), checkId);
    }

}
