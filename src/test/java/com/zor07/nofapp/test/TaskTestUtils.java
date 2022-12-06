package com.zor07.nofapp.test;

import com.zor07.nofapp.entity.levels.Level;
import com.zor07.nofapp.entity.levels.TaskContent;
import com.zor07.nofapp.entity.levels.Task;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskTestUtils {

    public static final Integer ORDER = 1;
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";

    public static final Integer NEW_ORDER = 2;
    public static final String NEW_NAME = "new name";
    public static final String NEW_DESCRIPTION = "new description";


    public static Task getBlankEntity(final TaskContent content, final Level level) {
        final var task = new Task();
        task.setName(NAME);
        task.setDescription(DESCRIPTION);
        task.setOrder(ORDER);
        task.setTaskContent(content);
        task.setLevel(level);
        return task;
    }

    public static void updateEntity(final Task task) {
        task.setOrder(NEW_ORDER);
        task.setName(NEW_NAME);
        task.setDescription(NEW_DESCRIPTION);
    }

    public static void checkUpdated(final Task task) {
        assertThat(task.getOrder()).isEqualTo(NEW_ORDER);
        assertThat(task.getName()).isEqualTo(NEW_NAME);
        assertThat(task.getDescription()).isEqualTo(NEW_DESCRIPTION);
    }

    public static void checkEntity(
            final Task actual,
            final Task expected,
            final boolean checkId
    ) {
        if (checkId) {
            assertThat(actual.getId()).isEqualTo(expected.getId());
        }
        assertThat(actual.getOrder()).isEqualTo(expected.getOrder());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
    }

}